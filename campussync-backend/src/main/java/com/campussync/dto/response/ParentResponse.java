package com.campussync.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentResponse {

    private Long parentId;

    private Long userId;

    private String username;

    private String email;

    private Boolean active;

    private String firstName;

    private String lastName;

    private String fatherName;

    private String motherName;

    private String phone;

    private String address;

    private String occupation;

    private Integer totalStudents;
}
