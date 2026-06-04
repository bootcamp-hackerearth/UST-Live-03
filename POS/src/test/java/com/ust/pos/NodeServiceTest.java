package com.ust.pos;

import com.ust.pos.dto.NodeDto;
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
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @InjectMocks
    private NodeServiceImpl nodeService;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock(lenient = true)
    private ModelMapper modelMapper;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getNodesForRolesSuccessTest() {
        UserDetails springUser =
                new org.springframework.security.core.userdetails.User(
                        "testuser", "password", List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(springUser, null)
        );

        User appUser = new User();
        appUser.setUsername("testuser");
        appUser.setRoles(List.of("ADMIN"));

        Node node1 = new Node();
        node1.setIdentifier("NODE1");
        node1.setRoles(List.of("ADMIN"));

        when(userRepository.findByUsername("testuser")).thenReturn(appUser);
        when(nodeRepository.findAll()).thenReturn(List.of(node1));
        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node1);
        when(modelMapper.map(node1, NodeDto.class)).thenReturn(new NodeDto());

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertEquals(1, result.size());
    }

    @Test
    void getNodesForRoles_UserWithNullRoles() {
        UserDetails springUser =
                new org.springframework.security.core.userdetails.User(
                        "testuser", "password", List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(springUser, null)
        );

        User appUser = new User();
        appUser.setUsername("testuser");
        appUser.setRoles(null);

        when(userRepository.findByUsername("testuser")).thenReturn(appUser);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRoles_PrincipalNull() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(null, null)
        );

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRoles_NoMatchingRoles() {
        UserDetails springUser =
                new org.springframework.security.core.userdetails.User(
                        "testuser", "password", List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(springUser, null)
        );

        User appUser = new User();
        appUser.setUsername("testuser");
        appUser.setRoles(List.of("USER"));

        Node node = new Node();
        node.setIdentifier("NODE1");
        node.setRoles(List.of("ADMIN"));

        when(userRepository.findByUsername("testuser")).thenReturn(appUser);
        when(nodeRepository.findAll()).thenReturn(List.of(node));

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRoles_NodeRolesNull() {
        UserDetails springUser =
                new org.springframework.security.core.userdetails.User(
                        "testuser", "password", List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(springUser, null)
        );

        User appUser = new User();
        appUser.setUsername("testuser");
        appUser.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setIdentifier("NODE1");
        node.setRoles(null);

        when(userRepository.findByUsername("testuser")).thenReturn(appUser);
        when(nodeRepository.findAll()).thenReturn(List.of(node));

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void saveSuccessTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Node node = new Node();
        node.setIdentifier("NODE1");

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(null);
        when(modelMapper.map(dto, Node.class)).thenReturn(node);

        NodeDto response = nodeService.save(dto);

        assertEquals("NODE1", response.getIdentifier());
        verify(nodeRepository).save(node);
    }

    @Test
    void saveFailureTest() {
        Node node = new Node();
        node.setIdentifier("NODE1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);

        NodeDto response = nodeService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        Node node = new Node();
        node.setIdentifier("NODE1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);

        NodeDto response = nodeService.update(dto);

        assertEquals("NODE1", response.getIdentifier());
        verify(modelMapper).map(dto, node);
        verify(nodeRepository).save(node);
    }

    @Test
    void updateFailureTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(null);

        NodeDto response = nodeService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        nodeService.delete("NODE1");
        verify(nodeRepository).deleteByIdentifier("NODE1");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Node node = new Node();
        node.setIdentifier("NODE1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        NodeDto response = nodeService.findByIdentifier("NODE1");

        assertEquals("NODE1", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(null);

        NodeDto response = nodeService.findByIdentifier("NODE1");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Node> nodes = List.of(new Node(), new Node());
        Page<Node> page = new PageImpl<>(nodes);

        List<NodeDto> dtoList = List.of(new NodeDto(), new NodeDto());

        when(nodeRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(
                eq(nodes),
                any(Type.class)
        )).thenReturn(dtoList);

        List<NodeDto> result = nodeService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(nodeRepository).findAll(pageable);
    }

}