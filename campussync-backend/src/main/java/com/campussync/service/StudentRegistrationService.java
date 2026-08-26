package com.campussync.service;

import com.campussync.dto.ParentRegistrationRequest;
import com.campussync.dto.StudentRegistrationRequest;
import com.campussync.entity.*;
import com.campussync.enums.Role;
import com.campussync.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        // 1. Create or Find Parent (if parent details are provided)
        Parent parent = null;
        if (request.getParentEmail() != null && !request.getParentEmail().isBlank()) {
            ParentRegistrationRequest parentRequest = ParentRegistrationRequest.builder()
                    .firstName(request.getParentFirstName())
                    .lastName(request.getParentLastName())
                    .email(request.getParentEmail())
                    .phone(request.getParentPhone())
                    .build();
            parent = createOrFindParent(parentRequest);
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

    private Parent createOrFindParent(ParentRegistrationRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            return parentRepository.findByUser(existingUser.get())
                    .orElseThrow(() -> new RuntimeException("Parent profile not found"));
        }

        return createNewParent(request);
    }

    private Parent createNewParent(ParentRegistrationRequest request) {
        User user = new User();
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.PARENT);
        user.setEmailVerified(false);
        User savedUser = userRepository.save(user);

        Parent parent = new Parent();
        parent.setFirstName(request.getFirstName());
        parent.setLastName(request.getLastName());
        parent.setPhone(request.getPhone());
        parent.setUser(savedUser);
        return parentRepository.save(parent);
    }
}
