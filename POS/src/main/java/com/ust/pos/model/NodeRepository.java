package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {
    Node findByIdentifier(String identifier);

    Node findByPathAndStatus(String path, boolean status);

    void deleteByIdentifier(String identifier);

    List<Node> findByStatusTrue();

    Page<Node> findByIsDeletedFalse(Pageable pageable);

    List<Node> findByStatusTrueAndIsDeletedFalse();

    Page<Node> findAll(Specification<Node> example, Pageable pageable);
}
