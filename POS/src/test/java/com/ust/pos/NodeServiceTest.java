package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private NodeServiceImpl nodeService;

    private Node nodeEntity;
    private NodeDto nodeDto;
    private User userEntity;

    @BeforeEach
    void setUp() {
        nodeEntity = new Node();
        nodeEntity.setId(1L);
        nodeEntity.setIdentifier("NODE-01");
        nodeEntity.setStatus(true);
        nodeEntity.setDeleted(false);
        nodeEntity.setRoles(Arrays.asList("ROLE_ADMIN", "ROLE_USER"));

        nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE-01");

        userEntity = new User();
        userEntity.setUsername("testuser");
        userEntity.setRoles(Collections.singletonList("ROLE_ADMIN"));
    }

    @Test
    void testFindByIdentifier() {
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(nodeEntity);

        NodeDto result = nodeService.findByIdentifier("NODE-01");

        assertNotNull(result);
        assertEquals("NODE-01", result.getIdentifier());
    }

    @Test
    void testSave_WhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> nodeService.save(null));
    }

    @Test
    void testSave_WhenIdentifierIsNull() {
        nodeDto.setIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> nodeService.save(nodeDto));
    }

    @Test
    void testSave_WhenNodeExistsAndNotDeleted() {
        nodeEntity.setDeleted(false);
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(nodeEntity);

        NodeDto result = nodeService.save(nodeDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenNodeExistsAndgetDeleted() {
        nodeEntity.setDeleted(true);
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(nodeEntity);

        NodeDto result = nodeService.save(nodeDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_Success() {
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(null);
        when(nodeRepository.save(any(Node.class))).thenReturn(nodeEntity);

        NodeDto result = nodeService.save(nodeDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Node created successfully", result.getMessage());
    }

    @Test
    void testUpdate_WhenNodeNotFound() {
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(null);

        NodeDto result = nodeService.update(nodeDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_WhenNodegetDeleted() {
        nodeEntity.setDeleted(true);
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(nodeEntity);

        NodeDto result = nodeService.update(nodeDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_Success() {
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(nodeEntity);
        when(nodeRepository.save(any(Node.class))).thenReturn(nodeEntity);

        NodeDto result = nodeService.update(nodeDto);

        assertNotNull(result);
        verify(nodeRepository, times(1)).save(any(Node.class));
    }

    @Test
    void testDelete() {
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(nodeEntity);
        when(nodeRepository.save(any(Node.class))).thenReturn(nodeEntity);

        nodeService.delete("NODE-01");

        verify(nodeRepository, times(1)).save(nodeEntity);
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Node> entityList = Collections.singletonList(nodeEntity);
        Page<Node> page = new PageImpl<>(entityList, pageable, 1);

        when(nodeRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(0, result.getPage());
    }

    @Test
    void testGetNodesForRoles_AuthenticationNull() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetNodesForRoles_Success() {
        Authentication authentication = mock(Authentication.class);
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("testuser", "password", new ArrayList<>());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(userEntity);
        when(nodeRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(Collections.singletonList(nodeEntity));
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(nodeEntity);

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("NODE-01", result.get(0).getIdentifier());
    }

    @Test
    void testToggleStatus() {
        nodeEntity.setStatus(true);
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(nodeEntity);
        when(nodeRepository.save(any(Node.class))).thenReturn(nodeEntity);

        NodeDto result = nodeService.toggleStatus("NODE-01");

        assertNotNull(result);
        assertFalse(result.getStatus());
    }

    @Test
    void testFindIfTrue() {
        List<Node> activeNodes = Collections.singletonList(nodeEntity);
        when(nodeRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(activeNodes);

        List<NodeDto> result = nodeService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}