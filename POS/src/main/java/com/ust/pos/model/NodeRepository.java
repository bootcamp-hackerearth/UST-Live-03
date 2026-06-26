package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {

    Node findByIdentifierAndDeletedFalse(String identifier);

    List<Node> findByDeletedFalse();

    Page<Node> findByDeletedFalse(Pageable pageable);
}