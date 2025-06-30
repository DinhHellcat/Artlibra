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
    @Transactional // <<== THÊM CHÚ THÍCH NÀY
    public void deleteProposal(Long proposalId) {
        // Lấy thông tin người dùng đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Tìm proposal cần xóa trong CSDL
        Proposal proposalToDelete = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("Proposal not found with id: " + proposalId));

        // Kiểm tra xem proposal có thực sự thuộc commission được chỉ định không
        // Dòng này không cần thiết với URL mới, nhưng giữ lại cũng không sao
        // if (!proposalToDelete.getCommission().getId().equals(commissionId)) {
        //     throw new IllegalArgumentException("Proposal does not belong to the specified commission.");
        // }

        // Kiểm tra quyền hạn
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        // Quyền của Họa sĩ: là người tạo ra proposal này
        boolean isProposalOwner = proposalToDelete.getArtist().getId().equals(currentUser.getId());

        // Quyền của Client: là chủ của commission mà proposal này thuộc về
        boolean isCommissionOwner = proposalToDelete.getCommission().getClient().getId().equals(currentUser.getId());

        // Nếu không có bất kỳ quyền nào ở trên, từ chối
        if (!isAdmin && !isProposalOwner && !isCommissionOwner) {
            throw new AccessDeniedException("You do not have permission to delete this proposal.");
        }

        // Nếu có quyền, tiến hành xóa
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
}