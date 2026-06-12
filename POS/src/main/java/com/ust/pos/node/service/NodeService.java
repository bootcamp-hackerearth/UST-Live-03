package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NodeService {
    NodeDto save(NodeDto nodeDto);

    WsDto<NodeDto> findAll(Pageable pageable);

    NodeDto findByIdentifier(String identifier);

    List<NodeDto> getNodesForRoles();

    NodeDto update(NodeDto nodeDto);

    NodeDto toggleStatus(String identifier);

    boolean delete(String username);
}
