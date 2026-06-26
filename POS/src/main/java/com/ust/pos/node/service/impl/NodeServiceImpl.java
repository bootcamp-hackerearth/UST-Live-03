package com.ust.pos.node.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.NodeService;
import jakarta.transaction.Transactional;
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
@Transactional
public class NodeServiceImpl extends BaseService implements NodeService {
    private final UserRepository userRepository;
    private final NodeRepository nodeRepository;
    private final ModelMapper modelMapper;

    public NodeServiceImpl(
            UserRepository userRepository,
            NodeRepository nodeRepository,
            ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.nodeRepository = nodeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<NodeDto> getNodesForRoles() {
        List<NodeDto> nodeDtos = new ArrayList<>();
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            org.springframework.security.core.userdetails.User principalObject =
                    (org.springframework.security.core.userdetails.User)
                            authentication.getPrincipal();
            if (principalObject != null) {
                findNodes(principalObject, nodeDtos);
            }
        }
        return nodeDtos;
    }

    private void findNodes(
            org.springframework.security.core.userdetails.User principalObject,
            List<NodeDto> nodeDtos) {
        User currentUser =
                userRepository.findByUsername(principalObject.getUsername());
        Set<String> nodesStr = new HashSet<>();
        List<Node> nodes =
                nodeRepository.findByDeletedFalse(Pageable.unpaged()).getContent();
        for (String role : currentUser.getRoles()) {
            for (Node node : nodes) {
                if (node.getRoles() != null
                        && node.getRoles().contains(role)) {
                    nodesStr.add(node.getIdentifier());
                }
            }
        }
        for (String nodeStr : nodesStr) {
            nodeDtos.add(modelMapper.map(nodeRepository.findByIdentifierAndDeletedFalse(nodeStr), NodeDto.class)
            );
        }
    }

    @Override
    public NodeDto save(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifier(identifier);
        if (existingNode != null) {
            if (Boolean.TRUE.equals(existingNode.getDeleted())) {
                nodeDto.setMessage(
                        "Node - " + identifier + " was deleted and cannot be recreated"
                );
            } else {
                nodeDto.setMessage(
                        "Node with identifier - " + identifier + " already exists"
                );
            }
            nodeDto.setSuccess(false);
            return nodeDto;
        }

        Node node = modelMapper.map(nodeDto, Node.class);
        setCreatedDetails(node);
        nodeRepository.save(node);
        nodeDto.setSuccess(true);
        return nodeDto;
    }

    @Override
    public NodeDto update(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingNode == null) {
            nodeDto.setMessage(
                    "Node with identifier - " + identifier + " not found"
            );
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        modelMapper.map(nodeDto, existingNode);
        setModifiedDetails(existingNode);
        nodeRepository.save(existingNode);
        nodeDto.setSuccess(true);
        return nodeDto;
    }

    @Override
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
        Page<Node> nodePage =
                nodeRepository.findByDeletedFalse(pageable);
        WsDto<NodeDto> wsDto = new WsDto<>();
        wsDto.setDtoList(
                modelMapper.map(nodePage.getContent(), listType)
        );
        wsDto.setTotalRecords(nodePage.getTotalElements());
        wsDto.setTotalPages(nodePage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public NodeDto findByIdentifier(String identifier) {
        return modelMapper.map(
                nodeRepository.findByIdentifierAndDeletedFalse(identifier),
                NodeDto.class
        );
    }
}