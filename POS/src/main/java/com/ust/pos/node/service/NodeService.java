package com.ust.pos.node.service;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NodeService {
    NodeDto save(NodeDto nodeDto);

    NodeDto update(NodeDto nodeDto);

    void delete(String username);
    
    List<NodeDto> getNodesForRoles();
    
    NodeDto findByIdentifier(String identifier);

    WsDto<NodeDto> findAll(Pageable pageable);

    NodeDto changeToggleStatus(String identifier, boolean status);

    List<NodeDto> findActiveStatus();
}

