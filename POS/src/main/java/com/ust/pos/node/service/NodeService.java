package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface NodeService {
    NodeDto save(NodeDto nodeDto);

    NodeDto update(NodeDto nodeDto);

    void delete(String identifier);

    WsDto<NodeDto> findAll(Pageable pageable);

    NodeDto findByIdentifier(String identifier);

    List<NodeDto> getNodesForRoles();

    WsDto<NodeDto> findAll(Specification<Node> example, Pageable pageable);
}