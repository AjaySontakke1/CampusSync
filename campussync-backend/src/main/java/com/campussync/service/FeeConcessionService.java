package com.campussync.service;

import com.campussync.dto.request.FeeConcessionDecisionDTO;
import com.campussync.dto.request.FeeConcessionRequestDTO;
import com.campussync.dto.response.FeeConcessionResponseDTO;
import com.campussync.entity.Fee;
import com.campussync.entity.FeeConcession;
import com.campussync.entity.User;
import com.campussync.enums.FeeConcessionStatus;
import com.campussync.enums.FeeStatus;
import com.campussync.exception.ResourceNotFoundException;
import com.campussync.repository.FeeConcessionRepository;
import com.campussync.repository.FeeRepository;
import com.campussync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeConcessionService {

    private final FeeConcessionRepository concessionRepository;
    private final FeeRepository feeRepository;
    private final UserRepository userRepository;

    @Transactional
    public FeeConcessionResponseDTO requestConcession(
            FeeConcessionRequestDTO request,
            String email) {

        Fee fee = feeRepository.findById(request.getFeeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Fee not found"));

        if (fee.getStudent() == null || fee.getStudent().getUser() == null
                || !fee.getStudent().getUser().getEmail().equals(email)) {
            boolean isParent = fee.getStudent() != null
                    && fee.getStudent().getParent() != null
                    && fee.getStudent().getParent().getUser() != null
                    && fee.getStudent().getParent().getUser().getEmail().equals(email);
            if (!isParent) {
                throw new AccessDeniedException(
                        "You can only request concessions for your own fee records");
            }
        }

        BigDecimal requestedAmount = request.getAmount();

        if (requestedAmount.compareTo(fee.getRemainingAmount()) > 0) {
            throw new IllegalArgumentException(
                    "Concession cannot exceed remaining fee");
        }

        FeeConcession concession = FeeConcession.builder()
                .fee(fee)
                .amount(requestedAmount)
                .reason(request.getReason())
                .status(FeeConcessionStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        concessionRepository.save(concession);

        return mapToResponse(concession);
    }

    @Transactional(readOnly = true)
    public List<FeeConcessionResponseDTO> getPendingRequests() {

        return concessionRepository
                .findByStatusOrderByRequestedAtAsc(
                        FeeConcessionStatus.PENDING
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public FeeConcessionResponseDTO decideConcession(
            Long concessionId,
            FeeConcessionDecisionDTO request,
            String adminEmail) {

        FeeConcession concession =
                concessionRepository.findById(concessionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Concession request not found"));

        if (concession.getStatus() != FeeConcessionStatus.PENDING) {
            throw new IllegalStateException(
                    "This request has already been processed");
        }

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Admin not found"));

        String decision = request.getDecision()
                .trim().toUpperCase();

        if ("APPROVE".equals(decision)) {

            Fee fee = concession.getFee();

            BigDecimal alreadyApproved =
                    concessionRepository
                            .findByFeeAndStatus(
                                    fee,
                                    FeeConcessionStatus.APPROVED
                            )
                            .stream()
                            .map(FeeConcession::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal newTotal =
                    alreadyApproved.add(concession.getAmount());

            BigDecimal maximumConcession =
                    fee.getTotalAmount().subtract(fee.getPaidAmount());

            if (newTotal.compareTo(maximumConcession) > 0) {
                throw new IllegalArgumentException(
                        "Approved concessions cannot exceed unpaid fee");
            }

            concession.setStatus(FeeConcessionStatus.APPROVED);
            concession.setApprovedBy(admin);
            concession.setApprovedAt(LocalDateTime.now());

            // Update actual outstanding balance
            fee.setRemainingAmount(
                    fee.getTotalAmount()
                            .subtract(fee.getPaidAmount())
                            .subtract(newTotal)
            );

            if (fee.getRemainingAmount()
                    .compareTo(BigDecimal.ZERO) == 0) {

                fee.setStatus(FeeStatus.PAID);

            } else if (fee.getDueDate().isBefore(LocalDate.now())) {

                fee.setStatus(FeeStatus.OVERDUE);

            } else if (fee.getPaidAmount()
                    .compareTo(BigDecimal.ZERO) > 0) {

                fee.setStatus(FeeStatus.PARTIALLY_PAID);

            } else {

                fee.setStatus(FeeStatus.PENDING);
            }

            feeRepository.save(fee);

        } else if ("REJECT".equals(decision)) {

            concession.setStatus(FeeConcessionStatus.REJECTED);

        } else {

            throw new IllegalArgumentException(
                    "Decision must be APPROVE or REJECT");
        }

        concession.setAdminRemarks(request.getAdminRemarks());

        concessionRepository.save(concession);

        return mapToResponse(concession);
    }

    private FeeConcessionResponseDTO mapToResponse(
            FeeConcession concession) {

        return FeeConcessionResponseDTO.builder()
                .concessionId(concession.getConcessionId())
                .feeId(concession.getFee().getFeeId())
                .studentId(
                        concession.getFee().getStudent() != null
                                ? concession.getFee().getStudent().getStudentId()
                                : null
                )
                .amount(concession.getAmount())
                .reason(concession.getReason())
                .status(concession.getStatus())
                .approvedBy(
                        concession.getApprovedBy() != null
                                ? concession.getApprovedBy().getEmail()
                                : null
                )
                .requestedAt(concession.getRequestedAt())
                .approvedAt(concession.getApprovedAt())
                .adminRemarks(concession.getAdminRemarks())
                .build();
    }
}
