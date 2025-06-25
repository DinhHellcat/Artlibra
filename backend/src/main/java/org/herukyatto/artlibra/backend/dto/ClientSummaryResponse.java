package org.herukyatto.artlibra.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientSummaryResponse {
    private Long id;
    private String fullName;
    private String avatarUrl;
}