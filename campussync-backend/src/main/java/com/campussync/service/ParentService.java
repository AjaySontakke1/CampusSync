package com.campussync.service;

import com.campussync.dto.AttendanceRecordDTO;
import com.campussync.dto.AttendanceResponseDTO;
import com.campussync.dto.StudentResponseDTO;
import com.campussync.entity.Attendance;
import com.campussync.entity.Parent;
import com.campussync.entity.Student;
import com.campussync.entity.User;
import com.campussync.enums.AttendanceStatus;
import com.campussync.repository.AttendanceRepository;
import com.campussync.repository.ParentRepository;
import com.campussync.repository.StudentRepository;
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

    public ParentService(
            UserRepository userRepository,
            ParentRepository parentRepository,
            StudentRepository studentRepository,
            AttendanceRepository attendanceRepository) {
        this.userRepository = userRepository;
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
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
}
