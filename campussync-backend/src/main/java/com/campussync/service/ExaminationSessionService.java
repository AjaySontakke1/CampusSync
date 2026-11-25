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
