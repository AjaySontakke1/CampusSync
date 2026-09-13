package com.campussync.service;

import com.campussync.dto.AnnouncementResponseDTO;
import com.campussync.dto.AssignmentProgressDTO;
import com.campussync.dto.AssignmentResponseDTO;
import com.campussync.dto.AttendanceRecordDTO;
import com.campussync.dto.AttendanceResponseDTO;
import com.campussync.dto.ExamResultResponseDTO;
import com.campussync.dto.FeeResponseDTO;
import com.campussync.dto.LectureNoteResponseDTO;
import com.campussync.dto.StudentResponseDTO;
import com.campussync.dto.TimetableResponseDTO;
import com.campussync.entity.Announcement;
import com.campussync.entity.Assignment;
import com.campussync.entity.AssignmentSubmission;
import com.campussync.entity.Attendance;
import com.campussync.entity.CourseEnrollment;
import com.campussync.entity.ExamResult;
import com.campussync.entity.Fee;
import com.campussync.entity.LectureNote;
import com.campussync.entity.Parent;
import com.campussync.entity.Student;
import com.campussync.entity.Timetable;
import com.campussync.entity.User;
import com.campussync.enums.AttendanceStatus;
import com.campussync.repository.AnnouncementRepository;
import com.campussync.repository.AssignmentRepository;
import com.campussync.repository.AssignmentSubmissionRepository;
import com.campussync.repository.AttendanceRepository;
import com.campussync.repository.CourseEnrollmentRepository;
import com.campussync.repository.ExamResultRepository;
import com.campussync.repository.FeeRepository;
import com.campussync.repository.LectureNoteRepository;
import com.campussync.repository.ParentRepository;
import com.campussync.repository.StudentRepository;
import com.campussync.repository.TimetableRepository;
import com.campussync.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParentService {

    private final UserRepository userRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final ExamResultRepository examResultRepository;
    private final FeeRepository feeRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;
    private final LectureNoteRepository lectureNoteRepository;
    private final TimetableRepository timetableRepository;
    private final AnnouncementRepository announcementRepository;

    public ParentService(
            UserRepository userRepository,
            ParentRepository parentRepository,
            StudentRepository studentRepository,
            AttendanceRepository attendanceRepository,
            ExamResultRepository examResultRepository,
            FeeRepository feeRepository,
            CourseEnrollmentRepository courseEnrollmentRepository,
            AssignmentRepository assignmentRepository,
            AssignmentSubmissionRepository assignmentSubmissionRepository,
            LectureNoteRepository lectureNoteRepository,
            TimetableRepository timetableRepository,
            AnnouncementRepository announcementRepository) {
        this.userRepository = userRepository;
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.examResultRepository = examResultRepository;
        this.feeRepository = feeRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.assignmentRepository = assignmentRepository;
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
        this.lectureNoteRepository = lectureNoteRepository;
        this.timetableRepository = timetableRepository;
        this.announcementRepository = announcementRepository;
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getMyStudents(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        List<Student> students = studentRepository.findByParent(parent);

        return students.stream()
                .map(student -> StudentResponseDTO.builder()
                        .id(student.getStudentId())
                        .firstName(student.getUser().getFirstName())
                        .lastName(student.getUser().getLastName())
                        .email(student.getUser().getEmail())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentResponseDTO getMyStudent(String email, Long studentId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        Student student = studentRepository
                .findByStudentIdAndParent(studentId, parent)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return StudentResponseDTO.builder()
                .id(student.getStudentId())
                .firstName(student.getUser().getFirstName())
                .lastName(student.getUser().getLastName())
                .email(student.getUser().getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public AttendanceResponseDTO getChildAttendance(String email, Long studentId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        Student student = studentRepository
                .findByStudentIdAndParent(studentId, parent)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Attendance> attendanceList = attendanceRepository.findByStudent(student);

        int totalClasses = attendanceList.size();
        int presentCount = (int) attendanceList.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                .count();
        int absentCount = totalClasses - presentCount;
        double percentage = totalClasses > 0 ? ((double) presentCount / totalClasses) * 100.0 : 0.0;

        List<AttendanceRecordDTO> records = attendanceList.stream()
                .map(a -> AttendanceRecordDTO.builder()
                        .attendanceId(a.getAttendanceId())
                        .subjectName(a.getSubject() != null ? a.getSubject().getSubjectName() : null)
                        .attendanceDate(a.getAttendanceDate())
                        .status(a.getStatus().name())
                        .remarks(a.getRemarks())
                        .build())
                .toList();

        return AttendanceResponseDTO.builder()
                .totalClasses(totalClasses)
                .presentCount(presentCount)
                .absentCount(absentCount)
                .attendancePercentage(Math.round(percentage * 100.0) / 100.0)
                .records(records)
                .build();
    }

    @Transactional(readOnly = true)
    public List<ExamResultResponseDTO> getChildExamResults(
            String email,
            Long studentId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        Student student = studentRepository
                .findByStudentIdAndParent(studentId, parent)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<ExamResult> results =
                examResultRepository.findByStudent(student);

        return results.stream()
                .map(result -> ExamResultResponseDTO.builder()
                        .resultId(result.getResultId())
                        .examTitle(result.getExam() != null ? result.getExam().getTitle() : null)
                        .examType(result.getExam() != null && result.getExam().getExamType() != null ? result.getExam().getExamType().name() : null)
                        .subjectName(
                                result.getExam() != null && result.getExam().getSubject() != null
                                        ? result.getExam().getSubject().getSubjectName()
                                        : null
                        )
                        .examDate(result.getExam() != null ? result.getExam().getExamDate() : null)
                        .marksObtained(result.getMarksObtained())
                        .maxMarks(result.getMaxMarks())
                        .grade(result.getGrade())
                        .status(result.getStatus() != null ? result.getStatus().name() : null)
                        .remarks(result.getRemarks())
                        .publishedAt(result.getPublishedAt())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FeeResponseDTO> getChildFees(
            String email,
            Long studentId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        Student student = studentRepository
                .findByStudentIdAndParent(studentId, parent)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Fee> fees = feeRepository.findByStudent(student);

        return fees.stream()
                .map(fee -> FeeResponseDTO.builder()
                        .feeId(fee.getFeeId())
                        .courseName(
                                fee.getCourse() != null
                                        ? fee.getCourse().getCourseName()
                                        : null
                        )
                        .semesterName(
                                fee.getSemester() != null
                                        ? fee.getSemester().getSemesterName()
                                        : null
                        )
                        .academicYear(
                                fee.getAcademicYear() != null
                                        ? fee.getAcademicYear().getName()
                                        : null
                        )
                        .totalAmount(fee.getTotalAmount())
                        .paidAmount(fee.getPaidAmount())
                        .remainingAmount(fee.getRemainingAmount())
                        .dueDate(fee.getDueDate())
                        .status(fee.getStatus().name())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseEnrollment getActiveEnrollment(
            String email,
            Long studentId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        Student student = studentRepository
                .findByStudentIdAndParent(studentId, parent)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return courseEnrollmentRepository
                .findByStudentAndActiveTrue(student)
                .orElseThrow(() ->
                        new RuntimeException("Active enrollment not found"));
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getChildAssignments(
            String email,
            Long studentId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        Student student = studentRepository
                .findByStudentIdAndParent(studentId, parent)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CourseEnrollment enrollment = courseEnrollmentRepository
                .findByStudentAndActiveTrue(student)
                .orElseThrow(() ->
                        new RuntimeException("Active enrollment not found"));

        List<Assignment> assignments =
                assignmentRepository.findByCourseAndSemesterAndAcademicYear(
                        enrollment.getCourse(),
                        enrollment.getSemester(),
                        enrollment.getAcademicYear()
                );

        return assignments.stream()
                .map(assignment -> AssignmentResponseDTO.builder()
                        .assignmentId(assignment.getAssignmentId())
                        .title(assignment.getTitle())
                        .description(assignment.getDescription())
                        .subjectName(
                                assignment.getSubject() != null
                                        ? assignment.getSubject().getSubjectName()
                                        : null
                        )
                        .teacherName(
                                assignment.getTeacher() != null && assignment.getTeacher().getUser() != null
                                        ? assignment.getTeacher().getUser().getFirstName()
                                        : null
                        )
                        .assignedDate(assignment.getAssignedDate())
                        .dueDate(assignment.getDueDate())
                        .attachmentUrl(assignment.getAttachmentUrl())
                        .active(assignment.isActive())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AssignmentProgressDTO> getChildAssignmentProgress(
            String email,
            Long studentId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        Student student = studentRepository
                .findByStudentIdAndParent(studentId, parent)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CourseEnrollment enrollment = courseEnrollmentRepository
                .findByStudentAndActiveTrue(student)
                .orElseThrow(() ->
                        new RuntimeException("Active enrollment not found"));

        List<Assignment> assignments =
                assignmentRepository.findByCourseAndSemesterAndAcademicYear(
                        enrollment.getCourse(),
                        enrollment.getSemester(),
                        enrollment.getAcademicYear()
                );

        List<AssignmentSubmission> submissions =
                assignmentSubmissionRepository.findByStudent(student);

        return assignments.stream()
                .map(assignment -> {

                    AssignmentSubmission submission = submissions.stream()
                            .filter(s -> s.getAssignment().getAssignmentId()
                                    .equals(assignment.getAssignmentId()))
                            .findFirst()
                            .orElse(null);

                    return AssignmentProgressDTO.builder()
                            .assignmentId(assignment.getAssignmentId())
                            .title(assignment.getTitle())
                            .description(assignment.getDescription())
                            .subjectName(
                                    assignment.getSubject() != null
                                            ? assignment.getSubject().getSubjectName()
                                            : null
                            )
                            .teacherName(
                                    assignment.getTeacher() != null
                                            && assignment.getTeacher().getUser() != null
                                            ? assignment.getTeacher()
                                                    .getUser()
                                                    .getFirstName()
                                            : null
                            )
                            .assignedDate(assignment.getAssignedDate())
                            .dueDate(assignment.getDueDate())
                            .attachmentUrl(assignment.getAttachmentUrl())
                            .active(assignment.isActive())

                            .submissionId(
                                    submission != null
                                            ? submission.getSubmissionId()
                                            : null
                            )
                            .submissionDate(
                                    submission != null
                                            ? submission.getSubmissionDate()
                                            : null
                            )
                            .fileUrl(
                                    submission != null
                                            ? submission.getFileUrl()
                                            : null
                            )
                            .submissionStatus(
                                    submission != null
                                            ? submission.getStatus().name()
                                            : null
                            )
                            .marks(
                                    submission != null
                                            ? submission.getMarks()
                                            : null
                            )
                            .feedback(
                                    submission != null
                                            ? submission.getFeedback()
                                            : null
                            )
                            .build();
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LectureNoteResponseDTO> getChildLectureNotes(
            String email,
            Long studentId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        Student student = studentRepository
                .findByStudentIdAndParent(studentId, parent)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CourseEnrollment enrollment = courseEnrollmentRepository
                .findByStudentAndActiveTrue(student)
                .orElseThrow(() ->
                        new RuntimeException("Active enrollment not found"));

        List<LectureNote> notes =
                lectureNoteRepository.findBySubjectSemesterAndAcademicYear(
                        enrollment.getSemester(),
                        enrollment.getAcademicYear()
                );

        return notes.stream()
                .map(note -> LectureNoteResponseDTO.builder()
                        .noteId(note.getNoteId())
                        .title(note.getTitle())
                        .description(note.getDescription())
                        .subjectName(
                                note.getSubject() != null
                                        ? note.getSubject().getSubjectName()
                                        : null
                        )
                        .teacherName(
                                note.getTeacher() != null
                                        && note.getTeacher().getUser() != null
                                        ? note.getTeacher()
                                                .getUser()
                                                .getFirstName()
                                        : null
                        )
                        .fileName(note.getFileName())
                        .fileType(note.getFileType())
                        .fileSize(note.getFileSize())
                        .fileUrl(note.getFileUrl())
                        .uploadedAt(note.getUploadedAt())
                        .active(note.isActive())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TimetableResponseDTO> getChildTimetable(
            String email,
            Long studentId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        Student student = studentRepository
                .findByStudentIdAndParent(studentId, parent)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CourseEnrollment enrollment = courseEnrollmentRepository
                .findByStudentAndActiveTrue(student)
                .orElseThrow(() ->
                        new RuntimeException("Active enrollment not found"));

        List<Timetable> timetableList = timetableRepository
                .findByCourseAndSemesterOrderByDayOfWeekAscStartTimeAsc(
                        enrollment.getCourse(),
                        enrollment.getSemester()
                );

        return timetableList.stream()
                .map(slot -> TimetableResponseDTO.builder()
                        .timetableId(slot.getTimetableId())
                        .subjectName(
                                slot.getSubject() != null
                                        ? slot.getSubject().getSubjectName()
                                        : null
                        )
                        .teacherName(
                                slot.getTeacher() != null && slot.getTeacher().getUser() != null
                                        ? slot.getTeacher().getUser().getFirstName()
                                        : null
                        )
                        .dayOfWeek(slot.getDayOfWeek())
                        .startTime(slot.getStartTime())
                        .endTime(slot.getEndTime())
                        .roomNumber(slot.getRoomNumber())
                        .division(slot.getDivision())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AnnouncementResponseDTO> getActiveAnnouncements(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Announcement> announcements =
                announcementRepository.findByActiveTrueOrderByCreatedAtDesc();

        return announcements.stream()
                .map(a -> AnnouncementResponseDTO.builder()
                        .announcementId(a.getAnnouncementId())
                        .title(a.getTitle())
                        .message(a.getMessage())
                        .priority(a.getPriority() != null ? a.getPriority().name() : null)
                        .createdAt(a.getCreatedAt())
                        .expiresAt(a.getExpiresAt())
                        .build())
                .toList();
    }
}
