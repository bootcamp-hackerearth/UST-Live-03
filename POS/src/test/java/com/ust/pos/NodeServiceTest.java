package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @InjectMocks
    private NodeServiceImpl nodeService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private ModelMapper modelMapper;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findByIdentifierTest() {

        Node node = new Node();
        node.setIdentifier("NODE001");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");

        when(nodeRepository.findByIdentifier("NODE001"))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        NodeDto result = nodeService.findByIdentifier("NODE001");

        Assertions.assertNotNull(result);

        Assertions.assertEquals(
                "NODE001",
                result.getIdentifier());
    }

    @Test
    void saveNewNodeTest() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");

        Node node = new Node();

        when(nodeRepository.findByIdentifier("NODE001"))
                .thenReturn(null);

        when(modelMapper.map(dto, Node.class))
                .thenReturn(node);

        when(nodeRepository.save(node))
                .thenReturn(node);

        NodeDto result = nodeService.save(dto);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(
                "NODE001",
                result.getIdentifier());

        verify(nodeRepository)
                .save(node);
    }

    @Test
    void saveDuplicateNodeTest() {

        Node existing = new Node();

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");

        when(nodeRepository.findByIdentifier("NODE001"))
                .thenReturn(existing);

        NodeDto result = nodeService.save(dto);

        Assertions.assertFalse(result.isSuccess());

        Assertions.assertEquals(
                "Node with identifier - NODE001 already exists",
                result.getMessage());

        verify(nodeRepository, never())
                .save(any());
    }

    @Test
    void updateExistingNodeTest() {

        Node existing = new Node();

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");

        when(nodeRepository.findByIdentifier("NODE001"))
                .thenReturn(existing);

        when(nodeRepository.save(existing))
                .thenReturn(existing);

        NodeDto result = nodeService.update(dto);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(
                "NODE001",
                result.getIdentifier());

        verify(modelMapper)
                .map(dto, existing);

        verify(nodeRepository)
                .save(existing);
    }

    @Test
    void updateNodeNotFoundTest() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");

        when(nodeRepository.findByIdentifier("NODE001"))
                .thenReturn(null);

        NodeDto result = nodeService.update(dto);

        Assertions.assertFalse(result.isSuccess());

        Assertions.assertEquals(
                "Node with identifier - NODE001 not found",
                result.getMessage());

        verify(nodeRepository, never())
                .save(any());
    }

    @Test
    void deleteTest() {

        doNothing().when(nodeRepository)
                .deleteByIdentifier("NODE001");

        nodeService.delete("NODE001");

        verify(nodeRepository)
                .deleteByIdentifier("NODE001");
    }

    @Test
    void findAllTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<Node> nodeList =
                List.of(new Node(), new Node());

        Page<Node> page =
                new PageImpl<>(
                        nodeList,
                        pageable,
                        2
                );

        List<NodeDto> dtoList =
                List.of(new NodeDto(), new NodeDto());

        when(nodeRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(nodeList), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<NodeDto> result =
                nodeService.findAll(pageable);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(
                2,
                result.getContent().size());

        Assertions.assertEquals(
                0,
                result.getPage());

        Assertions.assertEquals(
                10,
                result.getSizePerPage());

        Assertions.assertEquals(
                1,
                result.getTotalPages());

        Assertions.assertEquals(
                2,
                result.getTotalRecords());

        verify(nodeRepository)
                .findAll(pageable);

        verify(modelMapper)
                .map(eq(nodeList), any(Type.class));
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<Node> emptyList = List.of();

        Page<Node> page =
                new PageImpl<>(emptyList);

        when(nodeRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(emptyList), any(Type.class)))
                .thenReturn(List.of());

        WsDto<NodeDto> result =
                nodeService.findAll(pageable);

        Assertions.assertNotNull(result);

        Assertions.assertTrue(
                result.getContent().isEmpty());

        Assertions.assertEquals(
                0,
                result.getTotalRecords());
    }

    @Test
    void getNodesForRolesAuthenticationNullTest() {

        SecurityContextHolder.clearContext();

        List<NodeDto> result =
                nodeService.getNodesForRoles();

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

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null
                        )
                );

        when(userRepository.findByUsername("admin"))
                .thenReturn(null);

        List<NodeDto> result =
                nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRolesUserRolesNullTest() {

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "admin",
                        "password",
                        List.of()
                );

        User user = new User();
        user.setRoles(null);

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null
                        )
                );

        when(userRepository.findByUsername("admin"))
                .thenReturn(user);

        List<NodeDto> result =
                nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRolesNodeRolesNullTest() {

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "admin",
                        "password",
                        List.of()
                );

        User user = new User();
        user.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setIdentifier("NODE001");
        node.setRoles(null);

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null
                        )
                );

        when(userRepository.findByUsername("admin"))
                .thenReturn(user);

        when(nodeRepository.findAll())
                .thenReturn(List.of(node));

        List<NodeDto> result =
                nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRolesSuccessTest() {

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "admin",
                        "password",
                        List.of()
                );

        User user = new User();
        user.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setIdentifier("NODE001");
        node.setRoles(List.of("ADMIN"));

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null
                        )
                );

        when(userRepository.findByUsername("admin"))
                .thenReturn(user);

        when(nodeRepository.findAll())
                .thenReturn(List.of(node));

        when(nodeRepository.findByIdentifier("NODE001"))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        List<NodeDto> result =
                nodeService.getNodesForRoles();

        Assertions.assertEquals(
                1,
                result.size());

        Assertions.assertEquals(
                "NODE001",
                result.get(0).getIdentifier());
    }
}