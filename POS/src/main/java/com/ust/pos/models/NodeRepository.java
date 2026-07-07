package com.ust.pos.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {

    Node findByIdentifier(String identifier);

    Node findByIdentifierAndDeletedFalse(String identifier);

    List<Node> findAllByDeletedFalse();

    Page<Node> findAllByDeletedFalse(Pageable pageable);

    Page<Node> findAll(Specification <Node> example, Pageable pageable);

}
