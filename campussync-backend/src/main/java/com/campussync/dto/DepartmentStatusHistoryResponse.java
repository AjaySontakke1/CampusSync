package com.campussync.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentStatusHistoryResponse {

    private Long historyId;
    private Long departmentId;
    private String departmentName;
    private Boolean previousStatus;
    private Boolean newStatus;
    private Long changedByUserId;
    private String changedByEmail;
    private String changedByName;
    private String reason;
    private LocalDateTime changedAt;
}
