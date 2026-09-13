package com.campussync.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementResponseDTO {

    private Long announcementId;
    private String title;
    private String message;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
