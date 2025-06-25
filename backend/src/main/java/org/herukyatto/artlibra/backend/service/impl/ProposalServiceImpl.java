package org.herukyatto.artlibra.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.CreateProposalRequest;
import org.herukyatto.artlibra.backend.entity.*;
import org.herukyatto.artlibra.backend.exception.ResourceNotFoundException;
import org.herukyatto.artlibra.backend.repository.CommissionRepository;
import org.herukyatto.artlibra.backend.repository.ProposalRepository;
import org.herukyatto.artlibra.backend.service.ProposalService;
import org.springframework.security.access.AccessDeniedException; // <<== Đảm bảo có import này
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository proposalRepository;
    private final CommissionRepository commissionRepository;

    @Override
    public Proposal createProposal(Long commissionId, CreateProposalRequest request) {
        User currentArtist = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // === KIỂM TRA MỚI: YÊU CẦU EMAIL HỌA SĨ ĐÃ ĐƯỢC XÁC THỰC ===
        if (!currentArtist.isEmailVerified()) {
            throw new AccessDeniedException("You must verify your email before submitting a proposal.");
        }
        // ===========================================================

        Commission commission = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found with id: " + commissionId));

        if (commission.getStatus() != CommissionStatus.OPEN) {
            throw new IllegalStateException("This commission is no longer open for proposals.");
        }

        if (proposalRepository.existsByCommissionAndArtist(commission, currentArtist)) {
            throw new IllegalStateException("You have already submitted a proposal for this commission.");
        }

        Proposal proposal = Proposal.builder()
                .coverLetter(request.getCoverLetter())
                .proposedPrice(request.getProposedPrice())
                .estimatedCompletionDate(request.getEstimatedCompletionDate())
                .status(ProposalStatus.PENDING)
                .commission(commission)
                .artist(currentArtist)
                .build();

        return proposalRepository.save(proposal);
    }
}