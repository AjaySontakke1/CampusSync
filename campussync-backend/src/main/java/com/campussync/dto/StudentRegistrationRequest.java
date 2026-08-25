package com.campussync.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRegistrationRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Roll number is required")
    private String rollNumber;

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    private int admissionYear;
    private LocalDate dateOfBirth;
    private String phone;
    private String address;

    private String parentFirstName;
    private String parentLastName;
    private String parentEmail;
    private String parentPhone;
}
