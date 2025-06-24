package org.herukyatto.artlibra.backend.repository;

import org.herukyatto.artlibra.backend.entity.Role;
import org.herukyatto.artlibra.backend.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}