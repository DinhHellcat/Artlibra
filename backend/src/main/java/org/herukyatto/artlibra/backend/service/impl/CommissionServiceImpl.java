package org.herukyatto.artlibra.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.CreateCommissionRequest;
import org.herukyatto.artlibra.backend.entity.Commission;
import org.herukyatto.artlibra.backend.entity.CommissionStatus;
import org.herukyatto.artlibra.backend.entity.User;
import org.herukyatto.artlibra.backend.repository.CommissionRepository;
import org.herukyatto.artlibra.backend.service.CommissionService;
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
}