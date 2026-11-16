package com.campussync.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentStatusHistoryResponse {

    private Long historyId;

    private Boolean previousStatus;

    private Boolean newStatus;

    private String adminName;

    private String adminEmail;

    private String reason;

    private LocalDateTime changedAt;
}
