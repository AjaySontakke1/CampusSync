package com.campussync.service;

import com.campussync.dto.ParentRegistrationRequest;
import com.campussync.dto.StudentRegistrationRequest;
import com.campussync.entity.*;
import com.campussync.enums.Role;
import com.campussync.enums.TokenType;
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
    private final VerificationTokenService tokenService;
    private final EmailService emailService;

    public StudentRegistrationService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            ParentRepository parentRepository,
            CourseRepository courseRepository,
            SemesterRepository semesterRepository,
            VerificationTokenService tokenService,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.parentRepository = parentRepository;
        this.courseRepository = courseRepository;
        this.semesterRepository = semesterRepository;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    @Transactional
    public void registerStudent(StudentRegistrationRequest request) {

        // 1. Prevent duplicate student email.
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    "Student email is already registered");
        }

        // 2. Validate course and semester.
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Course not found"));

        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Semester not found"));

        // 3. Create or find the parent, if provided.
        Parent parent = null;

        if (request.getParentEmail() != null
                && !request.getParentEmail().isBlank()) {

            ParentRegistrationRequest parentRequest =
                    ParentRegistrationRequest.builder()
                            .firstName(request.getParentFirstName())
                            .lastName(request.getParentLastName())
                            .email(request.getParentEmail())
                            .phone(request.getParentPhone())
                            .build();

            parent = createOrFindParent(parentRequest);
        }

        // 4. Create the student user.
        User studentUser = new User();
        studentUser.setUsername(request.getUsername());
        studentUser.setEmail(request.getEmail());
        studentUser.setFirstName(request.getFirstName());
        studentUser.setLastName(request.getLastName());
        studentUser.setRole(Role.STUDENT);
        studentUser.setEmailVerified(false);
        studentUser.setActive(true);

        studentUser = userRepository.save(studentUser);

        // 5. Create the student profile.
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

        if (parent != null) {
            student.setParent(parent);
        }

        studentRepository.save(student);

        // 6. Generate the student's verification token.
        String studentToken = tokenService.generateToken();

        tokenService.createToken(
                studentUser,
                studentToken,
                TokenType.EMAIL_VERIFICATION);

        // 7. Send the student's verification link.
        emailService.sendVerificationEmail(
                studentUser.getEmail(),
                studentToken);
    }

    private Parent createOrFindParent(
            ParentRegistrationRequest request) {

        return userRepository.findByEmail(request.getEmail())
                .map(existingUser -> {

                    // The email must belong to a parent account.
                    if (existingUser.getRole() != Role.PARENT) {
                        throw new IllegalArgumentException(
                                "Parent email belongs to another account type");
                    }

                    Parent existingParent =
                            parentRepository.findByUser(existingUser)
                                    .orElseThrow(() ->
                                            new IllegalArgumentException(
                                                    "Parent profile not found"));

                    // If the parent is not verified, issue a fresh token.
                    if (!existingUser.isEmailVerified()) {
                        sendParentVerification(existingUser);
                    }

                    return existingParent;
                })
                .orElseGet(() -> createNewParent(request));
    }

    private Parent createNewParent(
            ParentRegistrationRequest request) {

        User user = new User();
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.PARENT);
        user.setEmailVerified(false);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        Parent parent = new Parent();
        parent.setFirstName(request.getFirstName());
        parent.setLastName(request.getLastName());
        parent.setPhone(request.getPhone());
        parent.setUser(savedUser);

        Parent savedParent = parentRepository.save(parent);

        // Verify automatically created parent account.
        sendParentVerification(savedUser);

        return savedParent;
    }

    private void sendParentVerification(User user) {

        String token = tokenService.generateToken();

        tokenService.createToken(
                user,
                token,
                TokenType.EMAIL_VERIFICATION);

        emailService.sendVerificationEmail(
                user.getEmail(),
                token);
    }
}
