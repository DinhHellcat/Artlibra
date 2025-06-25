package org.herukyatto.artlibra.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateProposalRequest {
    @NotNull(message = "Commission ID cannot be null") // <<== THÊM VÀO
    private Long commissionId;

    @NotBlank(message = "Cover letter cannot be blank")
    private String coverLetter;

    @NotNull(message = "Proposed price cannot be null")
    @DecimalMin(value = "0.0", message = "Proposed price must be non-negative")
    private BigDecimal proposedPrice;

    private LocalDate estimatedCompletionDate;
}