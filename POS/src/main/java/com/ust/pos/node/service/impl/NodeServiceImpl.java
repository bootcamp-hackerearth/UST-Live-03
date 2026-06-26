package com.ust.pos.node.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.NodeService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NodeServiceImpl extends BaseService implements NodeService {

    private static final String VALIDATION_MESSAGE = "Node with identifier - ";
    private final NodeRepository nodeRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public NodeServiceImpl(NodeRepository nodeRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.nodeRepository = nodeRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Override
    public NodeDto findByIdentifier(String identifier) {

        Node node = nodeRepository.findByIdentifier(identifier);
        return modelMapper.map(node, NodeDto.class);
    }

    public List<NodeDto> getNodesForRoles(Pageable pageable) {

        List<NodeDto> nodeDtos = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            if (principalObject != null) findNodes(principalObject, nodeDtos, pageable);
        }

        return nodeDtos;
    }

    private void findNodes(org.springframework.security.core.userdetails.User principalObject, List<NodeDto> nodeDtos, Pageable pageable) {

        User currentUser = userRepository.findByUsername(principalObject.getUsername());
        Set<String> nodesStr = new HashSet<>();
        Page<Node> nodePage = nodeRepository.findByIsDeletedFalse(pageable);
        List<Node> nodes = nodePage.getContent();

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
    public NodeDto save(NodeDto nodeDto) {

        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifier(identifier);

        if (existingNode != null) {
            nodeDto.setMessage(
                    existingNode.isDeleted()
                            ? VALIDATION_MESSAGE + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : VALIDATION_MESSAGE + identifier
                            + " already exists."
            );
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
            nodeDto.setMessage(VALIDATION_MESSAGE + identifier + " not found");
            nodeDto.setSuccess(false);
            return nodeDto;
        }

        modelMapper.map(nodeDto, existingNode);
        setModifiedDetails(existingNode);
        nodeRepository.save(existingNode);

        return nodeDto;
    }

    @Override
    public void toggleStatus(String identifier) {

        Node node = nodeRepository.findByIdentifier(identifier);

        if (node != null) {
            node.setStatus(!node.isStatus());
            nodeRepository.save(node);
        }
    }


    @Override
    @Transactional
    public void delete(String identifier) {

        Node node = nodeRepository.findByIdentifier(identifier);
        setModifiedDetails(node);
        softDelete(node);
    }

    @Override
    public WsDto<NodeDto> findAll(Pageable pageable) {

        Page<Node> nodesPage = nodeRepository.findByIsDeletedFalse(pageable);

        WsDto<NodeDto> nodesDto = new WsDto<>();

        List<NodeDto> nodesDtos = nodesPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, NodeDto.class))
                .toList();

        nodesDto.setContent(nodesDtos);
        nodesDto.setPage(nodesPage.getNumber());
        nodesDto.setSizePerPage(nodesPage.getSize());
        nodesDto.setTotalPages(nodesPage.getTotalPages());
        nodesDto.setTotalRecords(nodesPage.getTotalElements());

        return nodesDto;
    }
}
