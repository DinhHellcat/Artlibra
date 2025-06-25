package org.herukyatto.artlibra.backend.repository;

import org.herukyatto.artlibra.backend.entity.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {
    // Chúng ta sẽ thêm các phương thức truy vấn tùy chỉnh ở đây sau
}