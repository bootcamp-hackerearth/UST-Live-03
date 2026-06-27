package com.ust.pos.modell;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByIdentifierAndDeletedFalse(String identifier);

    Page<Role> findAllByDeletedFalse(Pageable pageable);

    Role findByIdentifier(String identifier);
}
