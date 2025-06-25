package org.herukyatto.artlibra.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.CreateCommissionRequest;
import org.herukyatto.artlibra.backend.entity.Commission;
import org.herukyatto.artlibra.backend.entity.CommissionStatus;
import org.herukyatto.artlibra.backend.entity.User;
import org.herukyatto.artlibra.backend.repository.CommissionRepository;
import org.herukyatto.artlibra.backend.service.CommissionService;
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
        // Lấy thông tin người dùng đang đăng nhập
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // === KIỂM TRA MỚI: YÊU CẦU EMAIL ĐÃ ĐƯỢC XÁC THỰC ===
        if (!currentUser.isEmailVerified()) {
            throw new AccessDeniedException("You must verify your email before creating a commission.");
        }
        // =======================================================

        // Xây dựng đối tượng Commission từ request DTO
        Commission commission = Commission.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .minBudget(request.getMinBudget())
                .maxBudget(request.getMaxBudget())
                .deadline(request.getDeadline())
                .client(currentUser) // Gán người dùng hiện tại làm client
                .status(CommissionStatus.OPEN) // Trạng thái ban đầu là MỞ
                .build();

        // Lưu vào CSDL và trả về
        return commissionRepository.save(commission);
    }

    @Override
    public void deleteCommission(Long commissionId) {
        // Lấy thông tin người dùng đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Tìm commission cần xóa trong CSDL
        Commission commissionToDelete = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new IllegalArgumentException("Commission not found with id: " + commissionId));

        // Kiểm tra quyền hạn
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = commissionToDelete.getClient().getId().equals(currentUser.getId());

        // Nếu người dùng không phải là Admin VÀ cũng không phải là chủ sở hữu, từ chối truy cập
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to delete this commission.");
        }

        // Nếu có quyền, tiến hành xóa
        commissionRepository.delete(commissionToDelete);
    }
}