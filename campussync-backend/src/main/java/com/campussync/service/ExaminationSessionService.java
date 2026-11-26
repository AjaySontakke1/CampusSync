package com.campussync.service;

import com.campussync.dto.request.ExaminationSessionRequestDTO;
import com.campussync.dto.response.ExaminationSessionResponseDTO;
import com.campussync.entity.AcademicYear;
import com.campussync.entity.ExaminationSession;
import com.campussync.entity.Semester;
import com.campussync.enums.ExamSessionStatus;
import com.campussync.exception.ResourceNotFoundException;
import com.campussync.repository.AcademicYearRepository;
import com.campussync.repository.ExaminationSessionRepository;
import com.campussync.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExaminationSessionService {

    private final ExaminationSessionRepository sessionRepository;
    private final SemesterRepository semesterRepository;
    private final AcademicYearRepository academicYearRepository;

    @Transactional
    public ExaminationSessionResponseDTO createSession(
            ExaminationSessionRequestDTO request) {

        // 1. Validate form dates
        if (request.getFormEndDate()
                .isBefore(request.getFormStartDate())) {

            throw new IllegalArgumentException(
                    "Form end date cannot be before start date");
        }

        // 2. Find semester
        Semester semester = semesterRepository
                .findById(request.getSemesterId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Semester not found"));

        // 3. Find academic year
        AcademicYear academicYear = academicYearRepository
                .findById(request.getAcademicYearId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Academic year not found"));

        // 4. Check duplicate session
        boolean exists =
                sessionRepository
                        .existsByExaminationNameAndSemesterAndAcademicYear(
                                request.getExaminationName(),
                                semester,
                                academicYear
                        );

        if (exists) {
            throw new IllegalArgumentException(
                    "This examination session already exists");
        }

        // 5. Create examination session
        ExaminationSession session = ExaminationSession.builder()
                .examinationName(request.getExaminationName())
                .semester(semester)
                .academicYear(academicYear)
                .formStartDate(request.getFormStartDate())
                .formEndDate(request.getFormEndDate())
                .status(ExamSessionStatus.DRAFT)
                .build();

        sessionRepository.save(session);

        return mapToResponse(session);
    }

    @Transactional(readOnly = true)
    public List<ExaminationSessionResponseDTO> getAllSessions() {

        return sessionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExaminationSessionResponseDTO> getAvailableSessions() {

        return sessionRepository.findByStatusOrderByFormStartDateAsc(ExamSessionStatus.OPEN)
                .stream()
                .filter(session -> !LocalDate.now().isAfter(session.getFormEndDate()))
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public ExaminationSessionResponseDTO openSession(Long sessionId) {

        ExaminationSession session =
                sessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Examination session not found"));

        if (session.getStatus() != ExamSessionStatus.DRAFT) {
            throw new IllegalStateException(
                    "Only DRAFT sessions can be opened");
        }

        if (LocalDate.now().isAfter(session.getFormEndDate())) {
            throw new IllegalStateException(
                    "Examination form deadline has already passed");
        }

        session.setStatus(ExamSessionStatus.OPEN);

        sessionRepository.save(session);

        return mapToResponse(session);
    }

    @Transactional
    public ExaminationSessionResponseDTO closeSession(Long sessionId) {

        ExaminationSession session =
                sessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Examination session not found"));

        if (session.getStatus() != ExamSessionStatus.OPEN) {
            throw new IllegalStateException(
                    "Only OPEN sessions can be closed");
        }

        session.setStatus(ExamSessionStatus.CLOSED);

        sessionRepository.save(session);

        return mapToResponse(session);
    }

    @Transactional
    public ExaminationSessionResponseDTO completeSession(Long sessionId) {

        ExaminationSession session =
                sessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Examination session not found"));

        if (session.getStatus() != ExamSessionStatus.CLOSED) {
            throw new IllegalStateException(
                    "Only CLOSED sessions can be completed");
        }

        session.setStatus(ExamSessionStatus.COMPLETED);

        sessionRepository.save(session);

        return mapToResponse(session);
    }

    @Transactional
    public ExaminationSession validateSessionForSubmission(
            Long sessionId) {

        ExaminationSession session =
                sessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Examination session not found"));

        LocalDate today = LocalDate.now();

        if (session.getStatus() != ExamSessionStatus.OPEN) {
            throw new IllegalStateException(
                    "Examination form is not currently open");
        }

        if (today.isBefore(session.getFormStartDate())) {
            throw new IllegalStateException(
                    "Examination form submission has not started");
        }

        if (today.isAfter(session.getFormEndDate())) {

            session.setStatus(ExamSessionStatus.CLOSED);

            throw new IllegalStateException(
                    "Examination form submission deadline has passed");
        }

        return session;
    }

    private ExaminationSessionResponseDTO mapToResponse(
            ExaminationSession session) {

        return ExaminationSessionResponseDTO.builder()
                .examinationSessionId(
                        session.getExaminationSessionId())
                .examinationName(session.getExaminationName())
                .semesterId(session.getSemester().getSemesterId())
                .semesterName(session.getSemester().getSemesterName())
                .academicYearId(
                        session.getAcademicYear().getAcademicYearId())
                .academicYear(session.getAcademicYear().getName())
                .formStartDate(session.getFormStartDate())
                .formEndDate(session.getFormEndDate())
                .status(session.getStatus())
                .build();
    }
}
