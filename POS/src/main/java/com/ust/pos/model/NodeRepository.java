package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {
    Node findByIdentifier(String identifier);

    List<Node> findByRoles(List<String> roles);

    void deleteByIdentifier(String identifier);

    List<Node> findByDeletedFalse();

    Page<Node> findByDeletedFalse(Pageable pageable);

    Page<Node> findByIdentifierContainingIgnoreCaseAndDeletedFalse(
            String identifier, Pageable pageable
    );

    Node findByIdentifierAndDeletedFalse(String identifier);
}
