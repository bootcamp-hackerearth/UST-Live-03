package com.ust.pos.node.service.impl;

import com.ust.pos.CommonService;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.NodeService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.BooleanUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NodeServiceImpl extends CommonService implements NodeService {
    private static final String NODE_WITH_IDENTIFIER = "Node with identifier - ";
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
        Node node =nodeRepository.findByIdentifier(identifier);
        if(node==null){
            throw new ResourceNotFoundException("Data Cannot Found");
        }
        return modelMapper.map(node, NodeDto.class);
    }

    @Override
    public NodeDto save(NodeDto nodeDto) {

        if (nodeDto == null || nodeDto.getIdentifier() == null) {
            throw new IllegalArgumentException("Identifier is required");
        }

        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifier(identifier);

        if (existingNode != null) {
            if (BooleanUtils.isNotTrue(existingNode.getDeleted())) {
                nodeDto.setMessage("Node with identifier '" + identifier + "' already exists");
                nodeDto.setSuccess(false);
                return nodeDto;
            }

            nodeDto.setMessage("Node was previously deleted. Please contact backend team to restore.");
            nodeDto.setSuccess(false);
            return nodeDto;
        }

        Node node = modelMapper.map(nodeDto, Node.class);
        setAuditFields(node, true);
        Node savedNode = nodeRepository.save(node);
        NodeDto response = modelMapper.map(savedNode, NodeDto.class);
        response.setSuccess(true);
        response.setMessage("Node created successfully");
        return response;
    }

    @Override
    public NodeDto update(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifier(identifier);
        if (existingNode == null) {
            nodeDto.setMessage(NODE_WITH_IDENTIFIER + "- " + identifier + " not found");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        if (BooleanUtils.isTrue(existingNode.getDeleted())) {
            nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + " was previously deleted. " + "Please contact backend team to restore.");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        modelMapper.map(nodeDto, existingNode);
        setAuditFields(existingNode, false);
        nodeRepository.save(existingNode);
        return nodeDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        Node node = nodeRepository.findByIdentifier(identifier);
        softDelete(node);
        setAuditFields(node, false);
        nodeRepository.save(node);
    }

    @Override
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

    public List<NodeDto> getNodesForRoles() {
        List<NodeDto> nodeDtos = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            if (principalObject != null) findNodes(principalObject, nodeDtos);
        }
        return nodeDtos;
    }

    private void findNodes(org.springframework.security.core.userdetails.User principalObject, List<NodeDto> nodeDtos) {
        User currentUser = userRepository.findByUsername(principalObject.getUsername());
        Set<String> nodesStr = new HashSet<>();
        List<Node> nodes = nodeRepository.findByStatusIsTrueAndDeletedFalse();
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
    public NodeDto toggleStatus(String identifier) {
        Node node = nodeRepository.findByIdentifier(identifier);
        node.setStatus(!node.getStatus());
        setAuditFields(node, false);
        nodeRepository.save(node);
        return modelMapper.map(node, NodeDto.class);
    }

    @Override
    public List<NodeDto> findIfTrue() {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        return modelMapper.map(nodeRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }
}