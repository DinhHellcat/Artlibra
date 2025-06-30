package org.herukyatto.artlibra.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.CreateProposalRequest;
import org.herukyatto.artlibra.backend.dto.ProposalSummaryResponse;
import org.herukyatto.artlibra.backend.entity.Commission;
import org.herukyatto.artlibra.backend.entity.Proposal;
import org.herukyatto.artlibra.backend.service.ProposalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commissions/{commissionId}/proposals")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    // POST /api/commissions/{commissionId}/proposals
    @PostMapping
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<Proposal> createProposal(
            @PathVariable Long commissionId,
            @Valid @RequestBody CreateProposalRequest request) {
        Proposal createdProposal = proposalService.createProposal(commissionId, request);
        return new ResponseEntity<>(createdProposal, HttpStatus.CREATED);
    }

    // GET /api/commissions/{commissionId}/proposals
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProposalSummaryResponse>> getProposalsForCommission(@PathVariable Long commissionId) {
        return ResponseEntity.ok(proposalService.getProposalsForCommission(commissionId));
    }

    // POST /api/commissions/{commissionId}/proposals/{proposalId}/accept
    @PostMapping("/{proposalId}/accept")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Commission> acceptProposal(
            @PathVariable Long commissionId,
            @PathVariable Long proposalId) {
        Commission updatedCommission = proposalService.acceptProposal(commissionId, proposalId);
        return ResponseEntity.ok(updatedCommission);
    }

    // DELETE /api/commissions/{commissionId}/proposals/{proposalId}
    @DeleteMapping("/{proposalId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteProposal(
            @PathVariable Long commissionId,
            @PathVariable Long proposalId) {
        proposalService.deleteProposal(commissionId, proposalId);
        return ResponseEntity.noContent().build();
    }
}