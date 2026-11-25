package com.campussync.service;

import com.campussync.dto.response.ExamFeeEligibilityResponseDTO;
import com.campussync.entity.*;
import com.campussync.enums.FeeConcessionStatus;
import com.campussync.exception.ResourceNotFoundException;
import com.campussync.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamEligibilityService {

    private final StudentRepository studentRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final FeeRepository feeRepository;
    private final FeeConcessionRepository concessionRepository;

    @Transactional(readOnly = true)
    public ExamFeeEligibilityResponseDTO checkEligibility(String email) {
        Student student = studentRepository.findByUser_Email(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));
        return checkEligibilityForStudent(student);
    }

    @Transactional(readOnly = true)
    public ExamFeeEligibilityResponseDTO checkEligibility(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));
        return checkEligibilityForStudent(student);
    }

    private ExamFeeEligibilityResponseDTO checkEligibilityForStudent(Student student) {

        // 2. Find current active enrollment
        List<CourseEnrollment> enrollments =
                enrollmentRepository.findAllByStudentAndActiveTrue(student);

        if (enrollments.isEmpty()) {
            throw new IllegalStateException(
                    "No active course enrollment found");
        }

        if (enrollments.size() > 1) {
            throw new IllegalStateException(
                    "Multiple active enrollments found. Contact Admin");
        }

        CourseEnrollment enrollment = enrollments.get(0);

        // 3. Get current semester details
        Course currentCourse = enrollment.getCourse();
        Semester currentSemester = enrollment.getSemester();
        AcademicYear currentYear = enrollment.getAcademicYear();

        // 4. Find all fee records belonging to this student
        List<Fee> fees = feeRepository.findByStudent(student);

        BigDecimal currentOutstanding = BigDecimal.ZERO;
        BigDecimal previousArrears = BigDecimal.ZERO;

        boolean currentFeeFound = false;

        // 5. Check every fee record
        for (Fee fee : fees) {

            BigDecimal approvedConcession =
                    getApprovedConcession(fee);

            // Calculate actual outstanding balance: Total - Paid - Approved Concession
            BigDecimal outstanding = fee.getTotalAmount()
                    .subtract(fee.getPaidAmount())
                    .subtract(approvedConcession);

            if (outstanding.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException(
                        "Invalid fee balance. Contact Admin");
            }

            // 6. Check whether this is the current semester fee
            boolean isCurrentFee =
                    fee.getCourse().getCourseId()
                            .equals(currentCourse.getCourseId())
                    &&
                    fee.getSemester().getSemesterId()
                            .equals(currentSemester.getSemesterId())
                    &&
                    fee.getAcademicYear().getAcademicYearId()
                            .equals(currentYear.getAcademicYearId());

            if (isCurrentFee) {

                currentFeeFound = true;
                currentOutstanding =
                        currentOutstanding.add(outstanding);

            } else if (isPreviousFee(fee, currentYear, currentSemester)) {

                // 7. Include previous semester arrears
                previousArrears =
                        previousArrears.add(outstanding);
            }
        }

        // 8. Calculate total outstanding
        BigDecimal totalOutstanding =
                currentOutstanding.add(previousArrears);

        // 9. Decide eligibility
        boolean eligible =
                currentFeeFound &&
                totalOutstanding.compareTo(BigDecimal.ZERO) == 0;

        String message;

        if (!currentFeeFound) {
            message = "Current semester fee record is not available. Contact Admin";
        } else if (eligible) {
            message = "You are eligible to fill the examination form";
        } else {
            message = "Clear current semester fees and previous arrears before filling the examination form";
        }

        // 10. Return eligibility result
        return ExamFeeEligibilityResponseDTO.builder()
                .studentId(student.getStudentId())
                .courseName(currentCourse.getCourseName())
                .semesterName(currentSemester.getSemesterName())
                .academicYear(currentYear.getName())
                .currentSemesterOutstanding(currentOutstanding)
                .previousArrears(previousArrears)
                .totalOutstanding(totalOutstanding)
                .eligible(eligible)
                .message(message)
                .build();
    }

    private BigDecimal getApprovedConcession(Fee fee) {

        return concessionRepository.findByFeeAndStatus(
                        fee,
                        FeeConcessionStatus.APPROVED
                )
                .stream()
                .map(FeeConcession::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isPreviousFee(
            Fee fee,
            AcademicYear currentYear,
            Semester currentSemester) {

        LocalDate feeYearStart =
                fee.getAcademicYear().getStartDate();

        LocalDate currentYearStart =
                currentYear.getStartDate();

        // A fee from an earlier academic year is an arrear
        if (feeYearStart.isBefore(currentYearStart)) {
            return true;
        }

        // In the same academic year, include earlier or
        // same-numbered semesters from another course.
        if (feeYearStart.equals(currentYearStart)) {
            return fee.getSemester().getSemesterNumber()
                    <= currentSemester.getSemesterNumber();
        }

        // Future academic-year fees do not block this exam
        return false;
    }
}
