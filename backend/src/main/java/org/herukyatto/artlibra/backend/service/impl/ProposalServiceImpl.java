package org.herukyatto.artlibra.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.CreateProposalRequest;
import org.herukyatto.artlibra.backend.entity.*;
import org.herukyatto.artlibra.backend.exception.ResourceNotFoundException;
import org.herukyatto.artlibra.backend.repository.CommissionRepository;
import org.herukyatto.artlibra.backend.repository.ProposalRepository;
import org.herukyatto.artlibra.backend.service.ProposalService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.herukyatto.artlibra.backend.dto.ArtistSummaryResponse;
import org.herukyatto.artlibra.backend.dto.ProposalSummaryResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.herukyatto.artlibra.backend.entity.ProposalStatus;

@Service
@RequiredArgsConstructor
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository proposalRepository;
    private final CommissionRepository commissionRepository;

    // SỬA LẠI PHƯƠNG THỨC NÀY
    @Override
    public Proposal createProposal(Long commissionId, CreateProposalRequest request) {
        User currentArtist = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!currentArtist.isEmailVerified()) {
            throw new AccessDeniedException("You must verify your email before submitting a proposal.");
        }

        Commission commission = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found with id: " + commissionId));

        // ... các logic kiểm tra khác giữ nguyên ...

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

    // SỬA LẠI PHƯƠNG THỨC NÀY
    @Override
    @Transactional
    public void deleteProposal(Long commissionId, Long proposalId) { // <<== SỬA LẠI CHỮ KÝ Ở ĐÂY
        // Lấy thông tin người dùng đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Tìm proposal cần xóa trong CSDL
        Proposal proposalToDelete = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("Proposal not found with id: " + proposalId));

        // Kiểm tra xem proposal có thực sự thuộc commission được chỉ định không
        if (!proposalToDelete.getCommission().getId().equals(commissionId)) {
            throw new IllegalArgumentException("Proposal does not belong to the specified commission.");
        }

        // Kiểm tra quyền hạn
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        boolean isProposalOwner = proposalToDelete.getArtist().getId().equals(currentUser.getId());

        boolean isCommissionOwner = proposalToDelete.getCommission().getClient().getId().equals(currentUser.getId());

        if (!isAdmin && !isProposalOwner && !isCommissionOwner) {
            throw new AccessDeniedException("You do not have permission to delete this proposal.");
        }

        proposalRepository.delete(proposalToDelete);
    }

    @Override
    @Transactional // Đảm bảo các lazy-loading hoạt động
    public List<ProposalSummaryResponse> getProposalsForCommission(Long commissionId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Commission commission = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found with id: " + commissionId));

        // Chỉ chủ của commission mới có quyền xem các proposal
        if (!commission.getClient().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to view proposals for this commission.");
        }

        List<Proposal> proposals = proposalRepository.findByCommissionId(commissionId);

        // Chuyển đổi danh sách Proposal Entity sang ProposalSummaryResponse DTO
        return proposals.stream()
                .map(this::convertToSummaryDto)
                .collect(Collectors.toList());
    }

    private ProposalSummaryResponse convertToSummaryDto(Proposal proposal) {
        ArtistSummaryResponse artistDto = new ArtistSummaryResponse(
                proposal.getArtist().getId(),
                proposal.getArtist().getFullName(),
                proposal.getArtist().getAvatarUrl()
        );

        return ProposalSummaryResponse.builder()
                .id(proposal.getId())
                .coverLetter(proposal.getCoverLetter())
                .proposedPrice(proposal.getProposedPrice())
                .estimatedCompletionDate(proposal.getEstimatedCompletionDate())
                .status(proposal.getStatus())
                .artist(artistDto)
                .build();
    }

    @Override
    @Transactional
    public Commission acceptProposal(Long commissionId, Long proposalId) {
        User currentClient = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Commission commission = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found with id: " + commissionId));

        Proposal acceptedProposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("Proposal not found with id: " + proposalId));

        // 1. Kiểm tra quyền: Chỉ chủ của commission mới được chấp nhận proposal
        if (!commission.getClient().getId().equals(currentClient.getId())) {
            throw new AccessDeniedException("You do not have permission to accept a proposal for this commission.");
        }

        // 2. Kiểm tra trạng thái: Chỉ chấp nhận khi commission đang MỞ
        if (commission.getStatus() != CommissionStatus.OPEN) {
            throw new IllegalStateException("This commission is no longer open for proposals.");
        }

        // 3. Cập nhật trạng thái của proposal được chấp nhận
        acceptedProposal.setStatus(ProposalStatus.ACCEPTED);
        proposalRepository.save(acceptedProposal);

        // 4. Cập nhật trạng thái của tất cả các proposal khác thành REJECTED
        commission.getProposals().stream()
                .filter(p -> !p.getId().equals(proposalId)) // Lọc ra các proposal khác
                .forEach(p -> {
                    p.setStatus(ProposalStatus.REJECTED);
                    proposalRepository.save(p);
                });

        // 5. Cập nhật commission với họa sĩ và giá đã chốt
        commission.setArtist(acceptedProposal.getArtist());
        commission.setAgreedPrice(acceptedProposal.getProposedPrice());
        commission.setStatus(CommissionStatus.PENDING_PAYMENT); // Chuyển sang trạng thái chờ thanh toán

        return commissionRepository.save(commission);
    }
}