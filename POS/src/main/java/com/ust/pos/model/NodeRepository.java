package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {

    Node findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Node> findByStatusAndIsDeleted(boolean status, boolean isDeleted);

    Page<Node> findByIsDeleted(boolean isDeleted, Pageable pageable);
}