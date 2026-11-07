package com.campussync.service;

import com.campussync.entity.Student;
import com.campussync.exception.ResourceNotFoundException;
import com.campussync.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentAttendanceService {

    private final StudentRepository studentRepository;

    public StudentAttendanceService(
            StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public Student getLoggedInStudent(String studentEmail) {

        return studentRepository.findByUserEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student profile not found"));
    }
}
