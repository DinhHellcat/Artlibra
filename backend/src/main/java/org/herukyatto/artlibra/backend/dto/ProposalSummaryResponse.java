package org.herukyatto.artlibra.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.herukyatto.artlibra.backend.entity.ProposalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProposalSummaryResponse {
    private Long id;
    private String coverLetter;
    private BigDecimal proposedPrice;
    private LocalDate estimatedCompletionDate;
    private ProposalStatus status;
    private ArtistSummaryResponse artist;
}