package com.campussync.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequestResponseDTO {

    private Long leaveRequestId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
    private String status;
    private LocalDateTime appliedAt;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private String remarks;
}
