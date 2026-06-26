package com.ust.pos.modell;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {
    Node findByIdentifier(String identifier);

    Node findByIdentifierAndDeletedFalse(String identifier);

    List<Node> findAllByDeletedFalse();

    Page<Node> findAllByDeletedFalse(Pageable pageable);
}