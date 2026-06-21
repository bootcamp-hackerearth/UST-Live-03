package com.ust.pos.node.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.NodeService;
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
public class NodeServiceImpl extends CommonService implements NodeService {

    public static final String NODE_WITH_IDENTIFIER = "Node with identifier '";
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<NodeDto> nodeDtos = new ArrayList<>();

        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User principalObject) {

            findEligibleNodes(principalObject, nodeDtos);
        }

        return nodeDtos;
    }

    private void findEligibleNodes(org.springframework.security.core.userdetails.User principalObject, List<NodeDto> nodeDtos) {

        User currentUser = userRepository.findByUsername(principalObject.getUsername());

        if (currentUser == null) {
            return;
        }

        Set<String> nodeIdentifiers = new HashSet<>();
        List<Node> nodes = nodeRepository.findAll();

        for (String role : currentUser.getRoles()) {
            for (Node node : nodes) {
                if (node.getRoles() != null && node.getRoles().contains(role) && !node.isDeleted()) {
                    nodeIdentifiers.add(node.getIdentifier());
                }
            }
        }

        for (String identifier : nodeIdentifiers) {
            Node node = nodeRepository.findByIdentifier(identifier);

            if (node != null) {
                nodeDtos.add(modelMapper.map(node, NodeDto.class));
            }
        }
    }

    @Override
    public NodeDto findByIdentifier(String identifier) {
        Node node = nodeRepository.findByIdentifier(identifier);

        if (node == null) {
            return null;
        }

        return modelMapper.map(node, NodeDto.class);
    }

    @Override
    public NodeDto save(NodeDto nodeDto) {

        Node existingNode = nodeRepository.findByIdentifier(nodeDto.getIdentifier());

        if (existingNode != null) {

            if (existingNode.isDeleted()) {
                nodeDto.setSuccess(false);
                nodeDto.setMessage(NODE_WITH_IDENTIFIER + nodeDto.getIdentifier() + "' has been soft deleted. Rollback by changing status.");
                return nodeDto;
            }

            nodeDto.setSuccess(false);
            nodeDto.setMessage(NODE_WITH_IDENTIFIER + nodeDto.getIdentifier() + "' already exists");
            return nodeDto;
        }

        Node node = modelMapper.map(nodeDto, Node.class);

        setAuditFields(node, true);
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
            nodeDto.setSuccess(false);
            nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + "' not found");
            return nodeDto;
        }

        modelMapper.map(nodeDto, existingNode);

        setAuditFields(existingNode, false);
        nodeRepository.save(existingNode);

        nodeDto.setSuccess(true);
        nodeDto.setMessage("Node updated successfully");

        return nodeDto;
    }

    @Override
    public boolean delete(String identifier) {

        Node node = nodeRepository.findByIdentifier(identifier);

        if (node == null) {
            return false;
        }

        softDelete(node);
        setAuditFields(node, false);

        nodeRepository.save(node);

        return true;
    }

    @Override
    public WsDto<NodeDto> findAll(Pageable pageable) {

        Page<Node> nodePage = nodeRepository.findByDeletedFalse(pageable);

        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();

        WsDto<NodeDto> wsDto = new WsDto<>();

        wsDto.setDtoList(modelMapper.map(nodePage.getContent(), listType));

        wsDto.setTotalRecords(nodePage.getTotalElements());
        wsDto.setTotalPages(nodePage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}