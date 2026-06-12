package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NodeService {
    List<NodeDto> getNodesForRoles();

    PaginationResponseDto<NodeDto> findAll(Pageable pageable);

    NodeDto save(NodeDto nodeDto);

    NodeDto update(NodeDto nodeDto);

    void delete(String identifier);

    NodeDto findByIdentifier(String identifier);
}