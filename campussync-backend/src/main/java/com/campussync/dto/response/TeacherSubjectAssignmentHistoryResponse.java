package com.campussync.dto.response;

import com.campussync.enums.AssignmentHistoryAction;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherSubjectAssignmentHistoryResponse {

    private Long historyId;

    private Long assignmentId;

    private AssignmentHistoryAction action;

    private Long changedById;

    private String changedByName;

    private String details;

    private String reason;

    private LocalDateTime changedAt;
}
