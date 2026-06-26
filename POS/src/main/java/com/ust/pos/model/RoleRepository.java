package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByIdentifier(String identifier);

    Role findByIdentifierAndDeletedFalse(String identifier);

    Page<Role> findByIdentifierContainingIgnoreCaseAndDeletedFalse(
            String identifier,
            Pageable pageable
    );

    Page<Role> findByDeletedFalse(Pageable pageable);

    List<Role> findByDeletedFalse();
}
