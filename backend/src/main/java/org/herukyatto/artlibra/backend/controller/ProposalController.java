package org.herukyatto.artlibra.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.CreateProposalRequest;
import org.herukyatto.artlibra.backend.entity.Proposal;
import org.herukyatto.artlibra.backend.service.ProposalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commissions/{commissionId}/proposals")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    @PostMapping
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<Proposal> createProposal(
            @PathVariable Long commissionId,
            @Valid @RequestBody CreateProposalRequest request) {
        Proposal createdProposal = proposalService.createProposal(commissionId, request);
        return new ResponseEntity<>(createdProposal, HttpStatus.CREATED);
    }
}