package org.herukyatto.artlibra.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.herukyatto.artlibra.backend.dto.CreatePortfolioItemRequest;
import org.herukyatto.artlibra.backend.entity.PortfolioItem;
import org.herukyatto.artlibra.backend.service.PortfolioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/portfolio/items")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<PortfolioItem> createPortfolioItem(
            @RequestPart("item") @Valid CreatePortfolioItemRequest request,
            @RequestPart("file") MultipartFile file
    ) {
        PortfolioItem createdItem = portfolioService.createPortfolioItem(request, file);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }
}