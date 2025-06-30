package org.herukyatto.artlibra.backend.repository;

import org.herukyatto.artlibra.backend.entity.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {
    // Sau này có thể thêm các phương thức tìm kiếm theo artist, tag...
}