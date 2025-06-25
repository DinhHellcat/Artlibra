package org.herukyatto.artlibra.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.CommissionSummaryResponse;
import org.herukyatto.artlibra.backend.dto.CreateCommissionRequest;
import org.herukyatto.artlibra.backend.entity.Commission;
import org.herukyatto.artlibra.backend.service.CommissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commissions")
@RequiredArgsConstructor
public class CommissionController {

    private final CommissionService commissionService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Commission> createCommission(@Valid @RequestBody CreateCommissionRequest request) {
        Commission createdCommission = commissionService.createCommission(request);
        return new ResponseEntity<>(createdCommission, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommission(@PathVariable Long id) {
        commissionService.deleteCommission(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CommissionSummaryResponse>> getOpenCommissions(Pageable pageable) {
        return ResponseEntity.ok(commissionService.getOpenCommissions(pageable));
    }
}