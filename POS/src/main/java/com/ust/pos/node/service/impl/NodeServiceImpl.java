package com.ust.pos.node.service.impl;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import com.ust.pos.base.service.BaseService;
import com.ust.pos.node.service.NodeService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NodeServiceImpl extends BaseService implements NodeService {
    private final UserRepository userRepository;
    private final NodeRepository nodeRepository;
    private final ModelMapper modelMapper;

    public NodeServiceImpl(UserRepository userRepository, NodeRepository nodeRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.nodeRepository = nodeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public NodeDto findByIdentifier(String identifier) {
        return modelMapper.map(nodeRepository.findByIdentifier(identifier), NodeDto.class);
    }

    @Override
    public NodeDto save(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifier(identifier);
        if (existingNode != null) {
            if (existingNode.isDeleted()) {
                nodeDto.setMessage("Node with identifier" + identifier + "has been soft deleted.(Rollback by changing status)");
                nodeDto.setSuccess(false);
                return nodeDto;
            }
            nodeDto.setMessage("Node with identifier - " + identifier + " already exists");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        Node node = modelMapper.map(nodeDto, Node.class);
        setCreatedDetails(node);
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
        String createdBy = existingNode.getCreatedBy();
        LocalDateTime createdOn = existingNode.getCreatedOn();
        modelMapper.map(nodeDto, existingNode);
        existingNode.setCreatedBy(createdBy);
        existingNode.setCreatedOn(createdOn);
        setModifiedDetails(existingNode);
        nodeRepository.save(existingNode);
        return modelMapper.map(existingNode, NodeDto.class);
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Node node = nodeRepository.findByIdentifier(identifier);
        softDelete(node);
        setModifiedDetails(node);
        nodeRepository.save(node);
    }

    public WsDto<NodeDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        Page<Node> nodePage = nodeRepository.findByDeletedFalse(pageable);

        WsDto<NodeDto> nodeWsDto = new WsDto<>();
        nodeWsDto.setDtoList(modelMapper.map(nodePage.getContent(), listType));
        nodeWsDto.setTotalRecords(nodePage.getTotalElements());
        nodeWsDto.setTotalPages(nodePage.getTotalPages());
        nodeWsDto.setSizePerPage(pageable.getPageSize());
        nodeWsDto.setPage(pageable.getPageNumber());

        return nodeWsDto;
    }

    public List<NodeDto> getNodesForRoles() throws NullPointerException {
        List<NodeDto> nodeDtos = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return nodeDtos;
        }
        if (authentication != null) {
            org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            if (principalObject != null) findNodes(principalObject, nodeDtos);
        }
        return nodeDtos;
    }

    private void findNodes(org.springframework.security.core.userdetails.User principalObject, List<NodeDto> nodeDtos) {
        User currentUser = userRepository.findByUsername(principalObject.getUsername());
        Set<String> nodesStr = new HashSet<>();
        List<Node> nodes = nodeRepository.findAll();
        for (String role : currentUser.getRoles()) {
            for (Node node : nodes) {
                if (node.getRoles() != null && node.getRoles().contains(role)) {
                    nodesStr.add(node.getIdentifier());
                }
            }
        }
        for (String nodeStr : nodesStr) {
            nodeDtos.add(modelMapper.map(nodeRepository.findByIdentifier(nodeStr), NodeDto.class));
        }
    }

    @Override
    public WsDto<NodeDto> findAll(Specification<Node> example, Pageable pageable) {

        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        Page<Node> page = nodeRepository.findAll(example, pageable);

        WsDto<NodeDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}