package org.herukyatto.artlibra.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.CreateCommissionRequest;
import org.herukyatto.artlibra.backend.entity.Commission;
import org.herukyatto.artlibra.backend.service.CommissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/commissions")
@RequiredArgsConstructor
public class CommissionController {

    private final CommissionService commissionService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')") // Chỉ những user có vai trò CLIENT mới được gọi API này
    public ResponseEntity<Commission> createCommission(@Valid @RequestBody CreateCommissionRequest request) {
        Commission createdCommission = commissionService.createCommission(request);
        return new ResponseEntity<>(createdCommission, HttpStatus.CREATED);
    }
}