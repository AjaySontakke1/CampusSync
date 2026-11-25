package com.campussync.entity;

import com.campussync.enums.FeeConcessionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_concessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeConcession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concession_id")
    private Long concessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_id", nullable = false)
    private Fee fee;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private FeeConcessionStatus status = FeeConcessionStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "admin_remarks", length = 500)
    private String adminRemarks;

    @PrePersist
    protected void onCreate() {
        if (this.requestedAt == null) {
            this.requestedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = FeeConcessionStatus.PENDING;
        }
    }
}
