package com.ust.pos.node.service.impl;


import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.model.*;
import com.ust.pos.node.service.NodeService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
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
public class NodeServiceImpl implements NodeService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<NodeDto> getNodesForRoles() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return new ArrayList<>();
        }
        Object principalObject = authentication.getPrincipal();

        if (!(principalObject instanceof org.springframework.security.core.userdetails.User)) {
            return new ArrayList<>();
        }
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) principalObject;

        User currentUser =
                userRepository.findByUsername(principal.getUsername());
        Set<Node> allowedNodes = new HashSet<>();
        List<Node> nodes = nodeRepository.findAll();
        for (String role : currentUser.getRoles()) {
            for (Node node : nodes) {
                if (node.getRoles().contains(role)) {
                    allowedNodes.add(node);
                }
            }
        }

        List<NodeDto> nodeDtos = new ArrayList<>();
        for (Node node : allowedNodes) {
            nodeDtos.add(modelMapper.map(node, NodeDto.class));
        }

        return nodeDtos;
    }
    @Override
    public NodeDto findByIdentifier(String identifier) {
        return modelMapper.map(nodeRepository.findByIdentifier(identifier), NodeDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Node node= nodeRepository.findByIdentifier(identifier);
        if (node != null) {
            boolean currentStatus = Boolean.TRUE.equals(node.getStatus());
            node.setStatus(!currentStatus);

            nodeRepository.save(node);
        }
    }

    @Override
    public List<NodeDto> findActiveNodes() {
        Type listType = new TypeToken<List<NodeDto>>() {}.getType();
        return modelMapper.map(nodeRepository.findByStatusTrue(),listType);
    }

    @Override
    public NodeDto save(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifier(identifier);
        if (existingNode != null) {
            nodeDto.setMessage("Node with identifier - " + identifier + " already exists");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        Node node = modelMapper.map(nodeDto, Node.class);
        nodeRepository.save(node);
        return nodeDto;
    }

    @Override
    public NodeDto update(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifier(identifier);
        if (existingNode == null) {
            nodeDto.setMessage("Node with identifier - " + identifier + " not found");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        modelMapper.map(nodeDto, existingNode);
        nodeRepository.save(existingNode);
        return nodeDto;
    }

    @Override
    public boolean delete(String identifier) {
        nodeRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public PageDto<NodeDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        Page<Node> nodePage = nodeRepository.findAll(pageable);
        PageDto<NodeDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(nodePage.getContent(), listType));
        pageDto.setTotalRecords(nodePage.getTotalElements());
        pageDto.setTotalPages(nodePage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }
}
