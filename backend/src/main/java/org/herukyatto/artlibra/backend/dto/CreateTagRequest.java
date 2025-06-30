package org.herukyatto.artlibra.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTagRequest {
    @NotBlank(message = "Tag name cannot be blank")
    private String name;
}