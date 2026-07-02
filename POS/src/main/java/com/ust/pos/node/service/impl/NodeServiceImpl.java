package com.ust.pos.node.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import com.ust.pos.node.service.NodeService;
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
    public NodeDto findByIdentifier(String identifier) {
        return modelMapper.map(nodeRepository.findByIdentifier(identifier), NodeDto.class);
    }

    @Override
    public NodeDto save(NodeDto nodeDto) {
        nodeDto.setIdentifier(nodeDto.getIdentifier().trim());
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifier(identifier);
        if (existingNode != null) {
            if (!existingNode.isDeleted()) {
                nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + " already exists");
                nodeDto.setSuccess(false);
                return nodeDto;
            }
            nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + " was previously deleted. " +
                    "Please contact backend team to restore.");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        Node node = modelMapper.map(nodeDto, Node.class);
        setAuditFields(node,true);
        nodeRepository.save(node);
        nodeDto.setSuccess(true);
        nodeDto.setMessage("Node created successfully");
        return nodeDto;
    }

    @Override
    public NodeDto update(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifier(identifier);
        if (existingNode == null) {
            nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + "not found");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        modelMapper.map(nodeDto, existingNode);
        setAuditFields(existingNode,false);
        nodeRepository.save(existingNode);
        return nodeDto;
    }

    @Override
    public boolean delete(String identifier) {
        Node node = nodeRepository.findByIdentifier(identifier);
        if (node == null) return false;
        softDelete(node);
        setAuditFields(node,false);
        nodeRepository.save(node);
        return true;
    }

    @Override
    public WsDto<NodeDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        Page<Node> nodePage = nodeRepository.findByDeletedFalse(pageable);
        WsDto<NodeDto> nodeDtoWsDto = new WsDto<>();
        nodeDtoWsDto.setDtoList(modelMapper.map(nodePage.getContent(), listType));
        nodeDtoWsDto.setTotalRecords(nodePage.getTotalElements());
        nodeDtoWsDto.setTotalPages(nodePage.getTotalPages());
        nodeDtoWsDto.setSizePerPage(pageable.getPageSize());
        nodeDtoWsDto.setPage(pageable.getPageNumber());
        return nodeDtoWsDto;
    }

    @Override
    public List<NodeDto> findIfTrue() {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        return modelMapper.map(nodeRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }

    @Override
    public WsDto<NodeDto> findAll(Specification<Node> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        Page<Node> nodePage = nodeRepository.findAll(example, pageable);
        WsDto<NodeDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(nodePage.getContent(), listType));
        wsDto.setTotalRecords(nodePage.getTotalElements());
        wsDto.setTotalPages(nodePage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }


    @Override
    public NodeDto toggleStatus(String identifier) {
        Node node = nodeRepository.findByIdentifier(identifier);
        node.setStatus(!node.isStatus());
        setAuditFields(node,false);
        nodeRepository.save(node);
        return modelMapper.map(node, NodeDto.class);
    }
}