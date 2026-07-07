package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface NodeService {

    List<NodeDto> getNodesForRoles();

    NodeDto save(NodeDto nodeDto);

    NodeDto update(NodeDto nodeDto);

    void delete(String username);

    WsDto<NodeDto> findAll(Pageable pageable);

    WsDto<NodeDto> findAll(Specification<Node> example, Pageable pageable);

    NodeDto findByIdentifier(String identifier);

    List<NodeDto> findIfTrue();

    NodeDto toggleStatus(String identifier);

}
