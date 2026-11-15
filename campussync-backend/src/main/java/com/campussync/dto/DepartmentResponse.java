package com.campussync.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponse {

    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    private String hodName;
    private Boolean active;
}
