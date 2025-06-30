package org.herukyatto.artlibra.backend.repository;

import org.herukyatto.artlibra.backend.entity.Commission;
import org.herukyatto.artlibra.backend.entity.Proposal;
import org.herukyatto.artlibra.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    // Phương thức để kiểm tra xem một artist đã ứng tuyển vào một commission hay chưa
    boolean existsByCommissionAndArtist(Commission commission, User artist);
    List<Proposal> findByCommissionId(Long commissionId);
}