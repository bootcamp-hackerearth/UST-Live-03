package com.ust.pos.node.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.NodeService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class NodeServiceImpl extends BaseService implements NodeService {

    public static final String NODE_WITH_IDENTIFIER = "Node with identifier - ";
    private final UserRepository userRepository;

    private final NodeRepository nodeRepository;

    private final ModelMapper modelMapper;

    public NodeServiceImpl(ModelMapper modelMapper, NodeRepository nodeRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.nodeRepository = nodeRepository;
        this.userRepository = userRepository;
    }

    public List<NodeDto> getNodesForRoles() {
        List<NodeDto> nodeDtos = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            if (principalObject != null) {
                User currentUser = userRepository.findByUsername(principalObject.getUsername());
                if (currentUser != null && currentUser.getRoles() != null) findEligibleNodes(currentUser, nodeDtos);

            }
        }
        return nodeDtos;
    }

    private void findEligibleNodes(User currentUser, List<NodeDto> nodeDtos) {
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
    public NodeDto findByIdentifier(String identifier) {
        Node node = nodeRepository.findByIdentifierAndIsDeletedFalse(identifier);
        if (node == null) {
            throw new ResourceNotFoundException("Node with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(node, NodeDto.class);
    }

    @Override
    public NodeDto findByPath(String path) {
        return modelMapper.map(nodeRepository.findByPath(path), NodeDto.class);
    }

    @Override
    public NodeDto save(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifier(identifier);
        if (existingNode != null) {
            nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + " already exists");
            if (existingNode.isDeleted()) {
                nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + " was deleted , Please Contact the Administrator to add.");
            }
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
            nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + " not found");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        modelMapper.map(nodeDto, existingNode);
        setModifiedDetails(existingNode);
        nodeRepository.save(existingNode);
        return nodeDto;
    }

    @Override
    public void delete(String identifier) {
        Node node = nodeRepository.findByIdentifier(identifier);
        softDelete(node);
        setModifiedDetails(node);
    }

    @Override
    public WsDto<NodeDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        Page<Node> nodePage = nodeRepository.findByIsDeletedFalse(pageable);
        WsDto<NodeDto> nodeWsDto = new WsDto<>();
        nodeWsDto.setDtoList(modelMapper.map(nodePage.getContent(), listType));
        nodeWsDto.setTotalRecords(nodePage.getTotalElements());
        nodeWsDto.setTotalPages(nodePage.getTotalPages());
        nodeWsDto.setSizePerPage(pageable.getPageSize());
        nodeWsDto.setPage(pageable.getPageNumber());
        return nodeWsDto;
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
