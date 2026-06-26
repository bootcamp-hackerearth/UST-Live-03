package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private NodeServiceImpl nodeService;

    private Node node;
    private NodeDto nodeDto;
    private User user;

    @BeforeEach
    void setUp() {
        node = new Node();
        node.setIdentifier("N1");
        node.setStatus(true);
        node.setDeleted(false);
        node.setRoles(List.of("ADMIN")); // roles in Node are List

        nodeDto = new NodeDto();
        nodeDto.setIdentifier("N1");

        user = new User();
        user.setUsername("testUser");
        user.setRoles(List.of("ADMIN")); // roles in User are Set ✅
    }
    // ✅ FIND BY IDENTIFIER
    @Test
    void testFindByIdentifier() {
        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto result = nodeService.findByIdentifier("N1");

        assertNotNull(result);
    }

    // ✅ FIND ALL (Pagination)
    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Node> page = new PageImpl<>(Collections.singletonList(node));

        when(nodeRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(nodeDto));

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
    }

    // ✅ SAVE - SUCCESS
    @Test
    void testSave_Success() {
        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        when(modelMapper.map(nodeDto, Node.class)).thenReturn(node);

        doNothing().when(nodeService).setAuditFields(node, true);

        NodeDto result = nodeService.save(nodeDto);

        assertNotNull(result);
        verify(nodeRepository).save(node);
    }

    // ✅ SAVE - ALREADY EXISTS
    @Test
    void testSave_AlreadyExists() {
        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    // ✅ SAVE - SOFT DELETED
    @Test
    void testSave_SoftDeleted() {
        node.setDeleted(true);

        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    // ✅ UPDATE - SUCCESS
    @Test
    void testUpdate_Success() {
        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);

        doNothing().when(modelMapper).map(nodeDto, node);
        doNothing().when(nodeService).setAuditFields(node, false);

        NodeDto result = nodeService.update(nodeDto);

        assertNotNull(result);
        verify(nodeRepository).save(node);
    }

    // ✅ UPDATE - NOT FOUND
    @Test
    void testUpdate_NotFound() {
        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);

        NodeDto result = nodeService.update(nodeDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    // ✅ DELETE
    @Test
    void testDelete() {
        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);

        doNothing().when(nodeService).softDelete(node);
        doNothing().when(nodeService).setAuditFields(node, false);

        nodeService.delete("N1");

        verify(nodeRepository).save(node);
    }

    // ✅ GET NODES FOR ROLES (SECURITY CONTEXT)
    @Test
    void testGetNodesForRoles() {
        // Mock Spring Security Context
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("testUser", "password", new ArrayList<>());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null)
        );

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(nodeRepository.findAll()).thenReturn(Collections.singletonList(node));
        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ✅ CHANGE TOGGLE STATUS
    @Test
    void testChangeToggleStatus() {
        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto result = nodeService.changeToggleStatus("N1", false);

        assertNotNull(result);
        assertFalse(node.isStatus());
        verify(nodeRepository).save(node);
    }

    // ✅ FIND ACTIVE STATUS
    @Test
    void testFindActiveStatus() {
        node.setStatus(true);
        Node inactive = new Node();
        inactive.setStatus(false);

        List<Node> nodes = List.of(node, inactive);

        when(nodeRepository.findAll()).thenReturn(nodes);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(nodeDto));

        List<NodeDto> result = nodeService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}