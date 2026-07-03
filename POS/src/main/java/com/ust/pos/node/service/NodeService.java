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

    void delete(String username);
    
    List<NodeDto> getNodesForRoles();
    
    NodeDto findByIdentifier(String identifier);

    WsDto<NodeDto> findAll(Pageable pageable);

    WsDto<NodeDto> findAll(Specification<Node>example, Pageable pageable);

    NodeDto changeToggleStatus(String identifier, boolean status);

    List<NodeDto> findActiveStatus();
}

