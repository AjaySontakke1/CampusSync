package com.campussync.service;

import com.campussync.dto.StudentRegistrationRequest;
import com.campussync.entity.*;
import com.campussync.enums.Role;
import com.campussync.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentRegistrationService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;

    public StudentRegistrationService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            ParentRepository parentRepository,
            CourseRepository courseRepository,
            SemesterRepository semesterRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.parentRepository = parentRepository;
        this.courseRepository = courseRepository;
        this.semesterRepository = semesterRepository;
    }

    @Transactional
    public void registerStudent(StudentRegistrationRequest request) {
        // 1. Create and save Parent User and Parent Profile (if parent details are provided)
        Parent parent = null;
        if (request.getParentEmail() != null && !request.getParentEmail().isBlank()) {
            User parentUser = userRepository.findByEmail(request.getParentEmail()).orElse(null);
            if (parentUser == null) {
                parentUser = new User();
                parentUser.setUsername(request.getParentEmail());
                parentUser.setEmail(request.getParentEmail());
                parentUser.setFirstName(request.getParentFirstName());
                parentUser.setLastName(request.getParentLastName());
                parentUser.setRole(Role.PARENT);
                parentUser.setEmailVerified(false);
                parentUser = userRepository.save(parentUser);
            }

            parent = parentRepository.findByUser(parentUser).orElse(null);
            if (parent == null) {
                parent = new Parent();
                parent.setFirstName(request.getParentFirstName());
                parent.setLastName(request.getParentLastName());
                parent.setPhone(request.getParentPhone());
                parent.setUser(parentUser);
                parent = parentRepository.save(parent);
            }
        }

        // 2. Create and save Student User
        User studentUser = new User();
        studentUser.setUsername(request.getUsername());
        studentUser.setEmail(request.getEmail());
        studentUser.setFirstName(request.getFirstName());
        studentUser.setLastName(request.getLastName());
        studentUser.setRole(Role.STUDENT);
        studentUser.setEmailVerified(false);
        studentUser = userRepository.save(studentUser);

        // 3. Fetch Course and Semester
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + request.getCourseId()));
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new IllegalArgumentException("Semester not found with ID: " + request.getSemesterId()));

        // 4. Create and save Student Profile
        Student student = new Student();
        student.setUser(studentUser);
        student.setRollNumber(request.getRollNumber());
        student.setRegistrationNumber(request.getRegistrationNumber());
        student.setCourse(course);
        student.setSemester(semester);
        student.setAdmissionYear(request.getAdmissionYear());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());

        // Connect Student -> Parent
        if (parent != null) {
            student.setParent(parent);
        }

        studentRepository.save(student);
    }
}
