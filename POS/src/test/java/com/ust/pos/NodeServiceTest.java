package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @InjectMocks
    private NodeServiceImpl nodeService;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void getNodesForRolesSuccessTest() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "admin",
                        "password",
                        List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                )
        );

        User user = new User();
        user.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setIdentifier("NODE1");
        node.setRoles(List.of("ADMIN"));

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        when(userRepository.findByUsername("admin"))
                .thenReturn(user);

        when(nodeRepository.findAll())
                .thenReturn(List.of(node));

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("NODE1", result.get(0).getIdentifier());
    }

    @Test
    void getNodesForRolesNoAuthenticationTest() {
        SecurityContextHolder.clearContext();

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRolesUserNotFoundTest() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "admin",
                        "password",
                        List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                )
        );

        when(userRepository.findByUsername("admin"))
                .thenReturn(null);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Node node = new Node();
        node.setIdentifier("NODE1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        NodeDto result = nodeService.findByIdentifier("NODE1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("NODE1", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(null);

        NodeDto result = nodeService.findByIdentifier("NODE1");

        Assertions.assertNull(result);
    }

    @Test
    void findByPathTest() {
        Node node = new Node();
        NodeDto dto = new NodeDto();

        when(nodeRepository.findByPath("/users"))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        NodeDto result = nodeService.findByPath("/users");

        Assertions.assertNotNull(result);
    }

    @Test
    void saveSuccessTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Node node = new Node();

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(null);

        when(modelMapper.map(dto, Node.class))
                .thenReturn(node);

        NodeDto result = nodeService.save(dto);

        Assertions.assertEquals("NODE1", result.getIdentifier());

        verify(nodeRepository).save(node);
    }

    @Test
    void saveAlreadyExistsTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Node existingNode = new Node();
        existingNode.setDeleted(false);

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(existingNode);

        NodeDto result = nodeService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Node with identifier - NODE1 already exists",
                result.getMessage()
        );

        verify(nodeRepository, never()).save(any());
    }

    @Test
    void saveDeletedNodeTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Node existingNode = new Node();
        existingNode.setDeleted(true);

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(existingNode);

        NodeDto result = nodeService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Node with identifier - NODE1 was deleted , Please Contact the Administrator to add.",
                result.getMessage()
        );

        verify(nodeRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Node existingNode = new Node();

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(existingNode);

        NodeDto result = nodeService.update(dto);

        Assertions.assertEquals("NODE1", result.getIdentifier());

        verify(modelMapper).map(dto, existingNode);
        verify(nodeRepository).save(existingNode);
    }

    @Test
    void updateFailureTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(null);

        NodeDto result = nodeService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Node with identifier - NODE1 not found",
                result.getMessage()
        );

        verify(nodeRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Node node = new Node();

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node);

        nodeService.delete("NODE1");

        verify(nodeRepository).findByIdentifier("NODE1");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Node> nodes = List.of(
                new Node(),
                new Node()
        );

        Page<Node> page = new PageImpl<>(nodes, pageable, 2);

        List<NodeDto> dtoList = List.of(
                new NodeDto(),
                new NodeDto()
        );

        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();

        when(nodeRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(nodes, listType))
                .thenReturn(dtoList);

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }
}