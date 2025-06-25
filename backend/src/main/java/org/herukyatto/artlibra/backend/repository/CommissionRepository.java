package org.herukyatto.artlibra.backend.repository;

import org.herukyatto.artlibra.backend.entity.Commission;
import org.herukyatto.artlibra.backend.entity.CommissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {
    // Tìm tất cả các commission có trạng thái cho trước, có phân trang và sắp xếp
    Page<Commission> findByStatus(CommissionStatus status, Pageable pageable);
}