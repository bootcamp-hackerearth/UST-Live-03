package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.model.Node;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface NodeService {

    List<NodeDto> getNodesForRoles();

    NodeDto save(NodeDto nodeDto);

    NodeDto update(NodeDto nodeDto);

    NodeDto delete(String identifier);

    PaginatedResponseDto<NodeDto> findAll(Pageable pageable);

    NodeDto findByIdentifier(String identifier);

    List<NodeDto> findAllActive();

    void changeStatus(String identifier, boolean status);

    PaginatedResponseDto<NodeDto> findAll(Specification<Node> example, Pageable pageable);
}