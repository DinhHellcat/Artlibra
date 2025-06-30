package org.herukyatto.artlibra.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class CreatePortfolioItemRequest {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    private String description;

    @NotEmpty(message = "At least one tag is required")
    private Set<Long> tagIds; // Một danh sách các ID của Tag
}