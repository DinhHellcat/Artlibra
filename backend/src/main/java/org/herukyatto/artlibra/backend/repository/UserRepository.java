package org.herukyatto.artlibra.backend.repository;

import org.herukyatto.artlibra.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
    Optional<User> findByEmailVerificationToken(String token); // <<== THÊM DÒNG NÀY
}