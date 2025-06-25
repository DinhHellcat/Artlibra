package org.herukyatto.artlibra.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.herukyatto.artlibra.backend.entity.CommissionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionDetailResponse {
    private Long id;
    private String title;
    private String description; // <<== Có thêm mô tả chi tiết
    private BigDecimal minBudget;
    private BigDecimal maxBudget;
    private LocalDate deadline;
    private CommissionStatus status;
    private ClientSummaryResponse client;
    // Sau này chúng ta sẽ thêm danh sách các họa sĩ đã ứng tuyển vào đây
}