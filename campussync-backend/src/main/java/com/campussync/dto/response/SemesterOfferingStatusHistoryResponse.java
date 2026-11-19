package com.campussync.dto.response;

import com.campussync.enums.SemesterStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemesterOfferingStatusHistoryResponse {

    private Long historyId;

    private SemesterStatus previousStatus;

    private SemesterStatus newStatus;

    private String changedBy;

    private String reason;

    private LocalDateTime changedAt;
}
