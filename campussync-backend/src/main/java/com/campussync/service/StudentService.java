package com.campussync.service;

import com.campussync.dto.AssignmentProgressDTO;
import com.campussync.entity.Assignment;
import com.campussync.entity.AssignmentSubmission;
import com.campussync.entity.CourseEnrollment;
import com.campussync.entity.Student;
import com.campussync.entity.User;
import com.campussync.repository.AssignmentRepository;
import com.campussync.repository.AssignmentSubmissionRepository;
import com.campussync.repository.CourseEnrollmentRepository;
import com.campussync.repository.StudentRepository;
import com.campussync.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;

    public StudentService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            CourseEnrollmentRepository courseEnrollmentRepository,
            AssignmentRepository assignmentRepository,
            AssignmentSubmissionRepository assignmentSubmissionRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.assignmentRepository = assignmentRepository;
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
    }

    @Transactional(readOnly = true)
    public List<AssignmentProgressDTO> getMyAssignments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        CourseEnrollment enrollment = courseEnrollmentRepository
                .findByStudentAndActiveTrue(student)
                .orElseThrow(() -> new RuntimeException("Active enrollment not found"));

        List<Assignment> assignments = assignmentRepository.findByCourseAndSemesterAndAcademicYear(
                enrollment.getCourse(),
                enrollment.getSemester(),
                enrollment.getAcademicYear()
        );

        List<AssignmentSubmission> submissions = assignmentSubmissionRepository.findByStudent(student);

        return assignments.stream()
                .map(assignment -> {
                    AssignmentSubmission submission = submissions.stream()
                            .filter(s -> s.getAssignment().getAssignmentId().equals(assignment.getAssignmentId()))
                            .findFirst()
                            .orElse(null);

                    return AssignmentProgressDTO.builder()
                            .assignmentId(assignment.getAssignmentId())
                            .title(assignment.getTitle())
                            .description(assignment.getDescription())
                            .subjectName(assignment.getSubject() != null ? assignment.getSubject().getSubjectName() : null)
                            .teacherName(assignment.getTeacher() != null && assignment.getTeacher().getUser() != null
                                    ? assignment.getTeacher().getUser().getFirstName()
                                    : null)
                            .assignedDate(assignment.getAssignedDate())
                            .dueDate(assignment.getDueDate())
                            .attachmentUrl(assignment.getAttachmentUrl())
                            .active(assignment.isActive())
                            .submissionId(submission != null ? submission.getSubmissionId() : null)
                            .submissionDate(submission != null ? submission.getSubmissionDate() : null)
                            .fileUrl(submission != null ? submission.getFileUrl() : null)
                            .submissionStatus(submission != null ? submission.getStatus().name() : null)
                            .marks(submission != null ? submission.getMarks() : null)
                            .feedback(submission != null ? submission.getFeedback() : null)
                            .build();
                })
                .toList();
    }
}
