package org.herukyatto.artlibra.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateCommissionRequest {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Minimum budget cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum budget must be positive")
    private BigDecimal minBudget;

    @NotNull(message = "Maximum budget cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Maximum budget must be positive")
    private BigDecimal maxBudget;

    @Future(message = "Deadline must be in the future")
    private LocalDate deadline;
}