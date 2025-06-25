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
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional; // <<== IMPORT MỚI

@Service
@RequiredArgsConstructor
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository proposalRepository;
    private final CommissionRepository commissionRepository;

    // SỬA LẠI PHƯƠNG THỨC NÀY
    @Override
    public Proposal createProposal(CreateProposalRequest request) {
        User currentArtist = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!currentArtist.isEmailVerified()) {
            throw new AccessDeniedException("You must verify your email before submitting a proposal.");
        }

        Commission commission = commissionRepository.findById(request.getCommissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found with id: " + request.getCommissionId()));

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
}