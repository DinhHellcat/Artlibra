package org.herukyatto.artlibra.backend.service;

import org.herukyatto.artlibra.backend.dto.CreatePortfolioItemRequest;
import org.herukyatto.artlibra.backend.entity.PortfolioItem;
import org.springframework.web.multipart.MultipartFile;

public interface PortfolioService {
    PortfolioItem createPortfolioItem(CreatePortfolioItemRequest request, MultipartFile file);
}