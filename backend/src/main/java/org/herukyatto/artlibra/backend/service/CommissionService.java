package org.herukyatto.artlibra.backend.service;

import org.herukyatto.artlibra.backend.dto.CommissionDetailResponse;
import org.herukyatto.artlibra.backend.dto.CommissionSummaryResponse;
import org.herukyatto.artlibra.backend.dto.CreateCommissionRequest;
import org.herukyatto.artlibra.backend.entity.Commission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommissionService {
    Commission createCommission(CreateCommissionRequest request);
    void deleteCommission(Long commissionId);
    Page<CommissionSummaryResponse> getOpenCommissions(Pageable pageable);
    CommissionDetailResponse getCommissionById(Long commissionId); // <<== Thêm phương thức này
}