package com.campussync.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentStudentLinkResponse {

    private Long studentId;
    private String studentName;
    private String rollNumber;

    private Long parentId;
    private String parentName;
    private String parentEmail;
}
