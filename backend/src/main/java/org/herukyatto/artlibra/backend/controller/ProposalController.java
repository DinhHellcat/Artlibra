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
import org.herukyatto.artlibra.backend.dto.ProposalSummaryResponse;
import java.util.List;

@RestController
//@RequestMapping("/api/proposals")
@RequestMapping("/api/commissions/{commissionId}/proposals")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    @PostMapping
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<Proposal> createProposal(
            @PathVariable Long commissionId, // <<== Lấy từ URL
            @Valid @RequestBody CreateProposalRequest request) {
        Proposal createdProposal = proposalService.createProposal(commissionId, request);
        return new ResponseEntity<>(createdProposal, HttpStatus.CREATED);
    }

    // === ENDPOINT MỚI ĐỂ LẤY DANH SÁCH PROPOSAL ===
    @GetMapping
    @PreAuthorize("isAuthenticated()") // Chỉ cần đăng nhập, service sẽ kiểm tra quyền chi tiết
    public ResponseEntity<List<ProposalSummaryResponse>> getProposalsForCommission(@PathVariable Long commissionId) {
        return ResponseEntity.ok(proposalService.getProposalsForCommission(commissionId));
    }
}