package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.Node;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface NodeService {
    NodeDto save(NodeDto nodeDto);

    NodeDto update(NodeDto nodeDto);

    void delete(String identifier);

    NodeDto findByIdentifier(String identifier);

    PaginationResponseDto<NodeDto> findAll(Pageable pageable);

    NodeDto updateStatus(String identifier, boolean status);

    List<NodeDto> getNodesForRoles();

    PaginationResponseDto<NodeDto> findAll(Specification<Node> example, Pageable pageable);
}
