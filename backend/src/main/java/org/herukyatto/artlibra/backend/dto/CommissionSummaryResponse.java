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
public class CommissionSummaryResponse {
    private Long id;
    private String title;
    private BigDecimal minBudget;
    private BigDecimal maxBudget;
    private LocalDate deadline;
    private CommissionStatus status;
    private ClientSummaryResponse client; // Thông tin tóm tắt về client
}