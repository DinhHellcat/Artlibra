package org.herukyatto.artlibra.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.ClientSummaryResponse;
import org.herukyatto.artlibra.backend.dto.CommissionDetailResponse;
import org.herukyatto.artlibra.backend.dto.CommissionSummaryResponse;
import org.herukyatto.artlibra.backend.dto.CreateCommissionRequest;
import org.herukyatto.artlibra.backend.entity.Commission;
import org.herukyatto.artlibra.backend.entity.CommissionStatus;
import org.herukyatto.artlibra.backend.entity.User;
import org.herukyatto.artlibra.backend.exception.ResourceNotFoundException;
import org.herukyatto.artlibra.backend.repository.CommissionRepository;
import org.herukyatto.artlibra.backend.service.CommissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommissionServiceImpl implements CommissionService {

    private final CommissionRepository commissionRepository;

    @Override
    public Commission createCommission(CreateCommissionRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currentUser.isEmailVerified()) {
            throw new AccessDeniedException("You must verify your email before creating a commission.");
        }
        Commission commission = Commission.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .minBudget(request.getMinBudget())
                .maxBudget(request.getMaxBudget())
                .deadline(request.getDeadline())
                .client(currentUser)
                .status(CommissionStatus.OPEN)
                .build();
        return commissionRepository.save(commission);
    }

    @Override
    public void deleteCommission(Long commissionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Commission commissionToDelete = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found with id: " + commissionId));
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = commissionToDelete.getClient().getId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to delete this commission.");
        }
        commissionRepository.delete(commissionToDelete);
    }

    @Override
    public Page<CommissionSummaryResponse> getOpenCommissions(Pageable pageable) {
        Page<Commission> commissionPage = commissionRepository.findByStatus(CommissionStatus.OPEN, pageable);
        return commissionPage.map(this::convertToSummaryDto);
    }

    // === PHƯƠNG THỨC MỚI ĐỂ LẤY CHI TIẾT ===
    @Override
    public CommissionDetailResponse getCommissionById(Long commissionId) {
        Commission commission = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found with id: " + commissionId));
        return convertToDetailDto(commission);
    }

    // --- CÁC HÀM TIỆN ÍCH CHUYỂN ĐỔI ---
    private CommissionSummaryResponse convertToSummaryDto(Commission commission) {
        ClientSummaryResponse clientDto = new ClientSummaryResponse(
                commission.getClient().getId(),
                commission.getClient().getFullName(),
                commission.getClient().getAvatarUrl()
        );
        return CommissionSummaryResponse.builder()
                .id(commission.getId())
                .title(commission.getTitle())
                .minBudget(commission.getMinBudget())
                .maxBudget(commission.getMaxBudget())
                .deadline(commission.getDeadline())
                .status(commission.getStatus())
                .client(clientDto)
                .build();
    }

    private CommissionDetailResponse convertToDetailDto(Commission commission) {
        ClientSummaryResponse clientDto = new ClientSummaryResponse(
                commission.getClient().getId(),
                commission.getClient().getFullName(),
                commission.getClient().getAvatarUrl()
        );
        return CommissionDetailResponse.builder()
                .id(commission.getId())
                .title(commission.getTitle())
                .description(commission.getDescription())
                .minBudget(commission.getMinBudget())
                .maxBudget(commission.getMaxBudget())
                .deadline(commission.getDeadline())
                .status(commission.getStatus())
                .client(clientDto)
                .build();
    }
}