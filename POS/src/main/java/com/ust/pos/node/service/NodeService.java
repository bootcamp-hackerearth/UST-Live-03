package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NodeService {
    List<NodeDto> getNodesForRoles();

    NodeDto save(NodeDto nodeDto);

    NodeDto update(NodeDto nodeDto);

    void delete(String identifier);

    WsDto<NodeDto> findAll(Pageable pageable);

    NodeDto findByIdentifier(String identifier);

    NodeDto changeNodeStatus(String identifier, boolean status);

    List<NodeDto> findActiveNode();
}
