package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface NodeService {
    List<NodeDto> getNodesForRoles();

    WsDto<NodeDto> findAll(Pageable pageable);

    NodeDto save(NodeDto nodeDto);

    NodeDto findByIdentifier(String identifier);

    NodeDto update(NodeDto nodeDto);

    void delete(String identifier);

    void toggleStatus(String identifier);

    WsDto<NodeDto> findAll(Specification<Node> example, Pageable pageable);
}
