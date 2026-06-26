package com.ust.pos.node.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.NodeDto;
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
public class NodeServiceImpl extends CommonService implements NodeService {

    private final UserRepository userRepository;
    private final NodeRepository nodeRepository;
    private final ModelMapper modelMapper;

    NodeServiceImpl(UserRepository userRepository, NodeRepository nodeRepository,
                    ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.nodeRepository = nodeRepository;
        this.userRepository = userRepository;
    }

    public List<NodeDto> getNodesForRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<NodeDto> nodeDtos = new ArrayList<>();

        if (authentication != null) {
            org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            if (principalObject != null) findEligibleNodes(principalObject, nodeDtos);
        }
        return nodeDtos;
    }

    private void findEligibleNodes(org.springframework.security.core.userdetails.User principalObject, List<NodeDto> nodeDtos) {
        User currentUser = userRepository.findByUsername(principalObject.getUsername());
        Set<String> nodesStr = new HashSet<>();
        List<Node> nodes = nodeRepository.findByIsDeleteFalse();

        for (String role : currentUser.getRoles()) {
            for (Node node : nodes) {
                if (node.getRoles() != null && node.getRoles().contains(role)) {
                    nodesStr.add(node.getIdentifier());
                }
            }
        }

        for (String nodeStr : nodesStr) {
            nodeDtos.add(modelMapper.map(nodeRepository.findByIdentifierAndIsDeleteFalse(nodeStr), NodeDto.class));
        }
    }

    @Override
    public NodeDto save(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (existingNode != null) {
            nodeDto.setMessage("Node with identifier - " + identifier + " already exists");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        Node node = modelMapper.map(nodeDto, Node.class);
        setAuditFields(node, true);
        nodeRepository.save(node);
        return nodeDto;
    }


    @Override
    public NodeDto update(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (existingNode == null) {
            nodeDto.setMessage("Node with identifier - " + identifier + " not found");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        modelMapper.map(nodeDto, existingNode);
        setAuditFields(existingNode, false);
        nodeRepository.save(existingNode);
        return nodeDto;
    }

    @Override
    public void delete(String identifier) {
        Node node = nodeRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (node != null) {
            node.setDelete(true);
            setAuditFields(node, false);
            nodeRepository.save(node);
        }
    }

    @Override
    public List<NodeDto> findAll() {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        return modelMapper.map(nodeRepository.findByIsDeleteFalse(), listType);
    }

    @Override
    public NodeDto findByIdentifier(String identifier) {
        return modelMapper.map(nodeRepository.
                findByIdentifierAndIsDeleteFalse(identifier), NodeDto.class);
    }

    @Override
    public Page<NodeDto> findAll(Pageable pageable, String search) {
        Page<Node> nodePage;
        if (search != null && !search.trim().isEmpty()) {
            nodePage = nodeRepository.findByIdentifierContainingIgnoreCaseAndIsDeleteFalse
                    (search, pageable);
        } else {
            nodePage = nodeRepository.findByIsDeleteFalse(pageable);
        }
        return nodePage.map(node -> modelMapper.map(node, NodeDto.class));
    }
}
