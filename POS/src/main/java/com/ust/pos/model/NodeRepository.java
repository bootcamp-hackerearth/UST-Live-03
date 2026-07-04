package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long>, JpaSpecificationExecutor<Node> {

    Node findByIdentifier(String identifier);

    List<Node> findByRoles(List<String> roles);

    void deleteByIdentifier(String identifier);

    Page<Node> findAll(Pageable pageable);

    Page<Node> findAll(Specification<Node> spec, Pageable pageable);

    Page<Node> findByDeletedFalse(Pageable pageable);
}