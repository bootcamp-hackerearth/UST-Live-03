package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private NodeRepository nodeRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NodeServiceImpl nodeService;

    private Node node;
    private NodeDto nodeDto;
    private User user;

    @BeforeEach
    void setUp() {
        nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE-001");
        nodeDto.setSuccess(true);

        node = new Node();
        node.setIdentifier("NODE-001");
        node.setRoles(List.of("ADMIN"));
        node.setDeleted(false);

        user = new User();
        user.setUsername("testuser");
        user.setRoles(List.of("ADMIN"));
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetNodesForRoles_NoAuthentication() {
        SecurityContextHolder.clearContext();

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertTrue(result.isEmpty());
        verifyNoInteractions(userRepository);
    }

    @Test
    void testGetNodesForRoles_PrincipalInvalidType() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymousUser", "password")
        );
        assertThrows(ClassCastException.class, () -> nodeService.getNodesForRoles());
    }

    @Test
    void testGetNodesForRoles_HappyPath_RoleMatch() {
        var principal = new org.springframework.security.core.userdetails.User(
                "testuser", "pass", Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null)
        );

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(nodeRepository.findAllByDeletedFalse()).thenReturn(List.of(node));
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertEquals(1, result.size());
        verify(nodeRepository, times(1)).findAllByDeletedFalse();
        verify(nodeRepository, times(1)).findByIdentifier("NODE-001");
    }

    @Test
    void testGetNodesForRoles_NodeRolesNull() {
        var principal = new org.springframework.security.core.userdetails.User(
                "testuser", "pass", Collections.emptyList());

        Node badNode = new Node();
        badNode.setIdentifier("NODE-002");
        badNode.setRoles(null);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null)
        );

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(nodeRepository.findAllByDeletedFalse()).thenReturn(List.of(badNode));

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertTrue(result.isEmpty());
        verify(nodeRepository, times(1)).findAllByDeletedFalse();
        verify(nodeRepository, never()).findByIdentifier(any());
    }

    @Test
    void testGetNodesForRoles_PrincipalObjectNull() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(null, null)
        );

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertTrue(result.isEmpty());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(nodeRepository);
    }

    @Test
    void testGetNodesForRoles_NodeRolesDoNotMatchUserRoles() {
        var principal = new org.springframework.security.core.userdetails.User(
                "testuser", "pass", Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null)
        );

        Node mismatchNode = new Node();
        mismatchNode.setIdentifier("NODE-999");
        mismatchNode.setRoles(List.of("GUEST", "MANAGER"));
        mismatchNode.setDeleted(false);

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(nodeRepository.findAllByDeletedFalse()).thenReturn(List.of(mismatchNode));

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertTrue(result.isEmpty());
        verify(nodeRepository, times(1)).findAllByDeletedFalse();
        verify(nodeRepository, never()).findByIdentifier(anyString());
    }

    @Test
    void testSave_AlreadyExists_Active() {
        node.setDeleted(false);
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        assertFalse(result.isSuccess());
        assertEquals("Node with identifier - NODE-001 already exists", result.getMessage());
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void testSave_AlreadyExists_SoftDeleted() {
        node.setDeleted(true);
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Node identifier - NODE-001 not available", result.getMessage());
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void testSave_NewNode() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(null);
        when(modelMapper.map(nodeDto, Node.class)).thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        assertTrue(result.isSuccess());
        verify(nodeRepository, times(1)).save(node);
    }

    @Test
    void testUpdate_NotFound() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(null);

        NodeDto result = nodeService.update(nodeDto);

        assertFalse(result.isSuccess());
        assertEquals("Node with identifier - NODE-001 not found", result.getMessage());
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void testUpdate_Found() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);

        NodeDto result = nodeService.update(nodeDto);

        assertTrue(result.isSuccess());
        verify(modelMapper, times(1)).map(nodeDto, node);
        verify(nodeRepository, times(1)).save(node);
    }

    @Test
    void testDelete_Success() {
        String identifier = "NODE-001";
        when(nodeRepository.findByIdentifierAndDeletedFalse(identifier)).thenReturn(node);

        assertDoesNotThrow(() -> nodeService.delete(identifier));

        verify(nodeRepository, times(1)).findByIdentifierAndDeletedFalse(identifier);
        assertTrue(node.getDeleted());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Node> page = new PageImpl<>(List.of(node), pageable, 1);
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();

        when(nodeRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(page.getContent(), listType)).thenReturn(List.of(nodeDto));

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        verify(nodeRepository, times(1)).findAllByDeletedFalse(pageable);
    }

    @Test
    void testFindByIdentifier() {
        when(nodeRepository.findByIdentifierAndDeletedFalse("NODE-001")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto result = nodeService.findByIdentifier("NODE-001");

        assertNotNull(result);
        assertEquals("NODE-001", result.getIdentifier());
        verify(nodeRepository, times(1)).findByIdentifierAndDeletedFalse("NODE-001");
    }

    @Test
    void testFindByIdentifier_Null() {
        when(nodeRepository.findByIdentifierAndDeletedFalse("NODE-001")).thenReturn(null);
        when(modelMapper.map(null, NodeDto.class)).thenReturn(null);

        assertNull(nodeService.findByIdentifier("NODE-001"));
        verify(nodeRepository, times(1)).findByIdentifierAndDeletedFalse("NODE-001");
    }
}