package com.campussync.service;

import com.campussync.dto.request.ExaminationFormRequestDTO;
import com.campussync.dto.response.ExamFeeEligibilityResponseDTO;
import com.campussync.dto.response.ExaminationFormResponseDTO;
import com.campussync.entity.ExaminationForm;
import com.campussync.entity.ExaminationSession;
import com.campussync.entity.Student;
import com.campussync.enums.ExaminationFormStatus;
import com.campussync.exception.ResourceNotFoundException;
import com.campussync.repository.ExaminationFormRepository;
import com.campussync.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExaminationFormService {

    private final ExaminationFormRepository formRepository;
    private final StudentRepository studentRepository;
    private final ExaminationSessionService sessionService;
    private final ExamEligibilityService eligibilityService;

    @Transactional
    public ExaminationFormResponseDTO submitForm(
            String email,
            ExaminationFormRequestDTO request) {

        // 1. Find authenticated student
        Student student = studentRepository.findByUser_Email(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found"));

        // 2. Validate examination session
        ExaminationSession session =
                sessionService.validateSessionForSubmission(
                        request.getExaminationSessionId());

        // 3. Check fee eligibility
        ExamFeeEligibilityResponseDTO eligibility =
                eligibilityService.checkEligibility(email);

        if (!eligibility.isEligible()) {
            throw new AccessDeniedException(
                    eligibility.getMessage());
        }

        // 4. Prevent duplicate submission
        boolean alreadySubmitted =
                formRepository.existsByStudentAndExaminationSession(
                        student, session);

        if (alreadySubmitted) {
            throw new IllegalStateException(
                    "Examination form already submitted");
        }

        // 5. Create examination form
        ExaminationForm form = ExaminationForm.builder()
                .student(student)
                .examinationSession(session)
                .submittedAt(LocalDateTime.now())
                .status(ExaminationFormStatus.SUBMITTED)
                .build();

        // 6. Save form
        formRepository.save(form);

        // 7. Return response
        return mapToResponse(form);
    }

    @Transactional(readOnly = true)
    public List<ExaminationFormResponseDTO> getMyForms(
            String email) {

        Student student = studentRepository.findByUser_Email(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found"));

        return formRepository
                .findByStudentOrderBySubmittedAtDesc(student)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ExaminationFormResponseDTO mapToResponse(
            ExaminationForm form) {

        ExaminationSession session =
                form.getExaminationSession();

        return ExaminationFormResponseDTO.builder()
                .examinationFormId(form.getExaminationFormId())
                .examinationName(session.getExaminationName())
                .semesterName(
                        session.getSemester().getSemesterName())
                .academicYear(
                        session.getAcademicYear().getName())
                .submittedAt(form.getSubmittedAt())
                .status(form.getStatus())
                .build();
    }
}
