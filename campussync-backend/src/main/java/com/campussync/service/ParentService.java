package com.campussync.service;

import com.campussync.dto.StudentResponseDTO;
import com.campussync.entity.Parent;
import com.campussync.entity.Student;
import com.campussync.entity.User;
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

    public ParentService(
            UserRepository userRepository,
            ParentRepository parentRepository,
            StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
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
}
