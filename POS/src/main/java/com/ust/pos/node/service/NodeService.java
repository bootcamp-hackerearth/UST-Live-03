package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface NodeService {
    NodeDto save(NodeDto nodeDto);

    WsDto<NodeDto> findAll(Pageable pageable);

    WsDto<NodeDto> findAll(Specification<Node> example, Pageable pageable);

    NodeDto findByIdentifier(String identifier);

    List<NodeDto> getNodesForRoles();

    NodeDto update(NodeDto nodeDto);

    NodeDto toggleStatus(String identifier);

    boolean delete(String username);
}
