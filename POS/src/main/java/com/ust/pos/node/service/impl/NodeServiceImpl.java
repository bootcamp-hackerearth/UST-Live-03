package com.ust.pos.node.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Node;
import com.ust.pos.modell.NodeRepository;
import com.ust.pos.modell.UserRepository;
import com.ust.pos.node.service.NodeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NodeServiceImpl extends BaseService implements NodeService {

    public static final String NODE_WITH_IDENTIFIER = "Node with identifier - ";

    private final UserRepository userRepository;
    private final NodeRepository nodeRepository;
    private final ModelMapper modelMapper;

    public List<NodeDto> getNodesForRoles() {
        List<NodeDto> nodeDtos = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            if (principalObject != null) findNodes(principalObject, nodeDtos);
        }
        return nodeDtos;
    }

    private void findNodes(
            org.springframework.security.core.userdetails.User principalObject,
            List<NodeDto> nodeDtos
    ) {
        com.ust.pos.modell.User currentUser = userRepository.findByUsername(principalObject.getUsername());
        Set<String> nodesStr = new HashSet<>();
        List<Node> nodes = nodeRepository.findAllByDeletedFalse();
        for (String role : currentUser.getRoles()) {
            for (Node node : nodes) {
                if (node.getRoles() != null && node.getRoles().contains(role)) {
                    nodesStr.add(node.getIdentifier());
                }
            }
        }
        for (String nodeStr : nodesStr) {
            nodeDtos.add(
                    modelMapper.map(
                            nodeRepository.findByIdentifierAndDeletedFalse(nodeStr),
                            NodeDto.class
                    )
            );
        }
    }

    @Override
    public NodeDto findByIdentifier(String identifier) {
        return modelMapper.map(nodeRepository.findByIdentifierAndDeletedFalse(identifier), NodeDto.class
        );
    }

    @Override
    public NodeDto save(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifier(identifier);

        if (existingNode != null) {
            if (Boolean.TRUE.equals(existingNode.getDeleted())) {
                nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + " was deleted and cannot be created again.");
                nodeDto.setSuccess(false);
                return nodeDto;
            }
            nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + " already exists");
            nodeDto.setSuccess(false);
            return nodeDto;
        }

        Node node = modelMapper.map(nodeDto, Node.class);
        if (node.getStatus() == null) {
            node.setStatus(true);
        }

        setCreatedDetails(node);
        nodeRepository.save(node);
        return nodeDto;
    }

    @Override
    public NodeDto update(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingNode == null) {
            nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + " not found");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        String originalCreatedBy = existingNode.getCreatedBy();
        java.time.LocalDateTime originalCreatedOn = existingNode.getCreatedOn();
        modelMapper.map(nodeDto, existingNode);
        existingNode.setCreatedBy(originalCreatedBy);
        existingNode.setCreatedOn(originalCreatedOn);
        setModifiedDetails(existingNode);
        nodeRepository.save(existingNode);
        return nodeDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Node node = nodeRepository.findByIdentifierAndDeletedFalse(identifier);

        if (node != null) {
            softDelete(node);
            setModifiedDetails(node);
            nodeRepository.save(node);
        }
    }

    @Override
    public WsDto<NodeDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        Page<Node> nodePage = nodeRepository.findAllByDeletedFalse(pageable);
        WsDto<NodeDto> nodeWsDto = new WsDto<>();
        nodeWsDto.setDtoList(modelMapper.map(nodePage.getContent(), listType));
        nodeWsDto.setTotalRecords(nodePage.getTotalElements());
        nodeWsDto.setTotalPage(nodePage.getTotalPages());
        nodeWsDto.setSizePerPage(pageable.getPageSize());
        nodeWsDto.setPage(pageable.getPageNumber());
        return nodeWsDto;
    }
}