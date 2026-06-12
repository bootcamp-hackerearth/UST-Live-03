package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private NodeRepository nodeRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private NodeServiceImpl nodeService;

    private Node node;
    private NodeDto nodeDto;
    private User user;

    @BeforeEach
    void setUp() {
        nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE-001");

        node = new Node();
        node.setIdentifier("NODE-001");
        node.setRoles(List.of("ADMIN"));

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
    void testGetNodesForRoles_PrincipalNull() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(null, null)
        );

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertTrue(result.isEmpty());
        verifyNoInteractions(userRepository);
    }

    @Test
    void testGetNodesForRoles_HappyPath_RoleMatch() {

        var principal = new org.springframework.security.core.userdetails.User(
                "testuser", "pass", Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null)
        );

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(nodeRepository.findAll()).thenReturn(List.of(node));
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertEquals(1, result.size());

        verify(nodeRepository).findAll();
        verify(nodeRepository).findByIdentifier("NODE-001");
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

        user.setRoles(List.of("ADMIN"));

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(nodeRepository.findAll()).thenReturn(List.of(badNode));

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertTrue(result.isEmpty());
        verify(nodeRepository).findAll();
        verify(nodeRepository, never()).findByIdentifier(any());
    }

    @Test
    void testSave_AlreadyExists() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        assertFalse(result.isSuccess());
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void testSave_NewNode() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(null);
        when(modelMapper.map(nodeDto, Node.class)).thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        assertTrue(result.isSuccess());
        verify(nodeRepository).save(node);
    }

    @Test
    void testUpdate_NotFound() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(null);

        NodeDto result = nodeService.update(nodeDto);

        assertFalse(result.isSuccess());
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void testUpdate_Found() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);

        NodeDto result = nodeService.update(nodeDto);

        assertTrue(result.isSuccess());
        verify(modelMapper).map(nodeDto, node);
        verify(nodeRepository).save(node);
    }

    @Test
    void testDelete() {
        boolean result = nodeService.delete("NODE-001");

        assertTrue(result);
        verify(nodeRepository).deleteByIdentifier("NODE-001");
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Node> page =
                new PageImpl<>(List.of(node), pageable, 1);

        Type listType =
                new TypeToken<List<NodeDto>>() {
                }.getType();

        when(nodeRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(nodeDto));

        WsDto<NodeDto> result =
                nodeService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());

        verify(nodeRepository).findAll(pageable);

        verify(modelMapper)
                .map(page.getContent(), listType);
    }

    @Test
    void testFindByIdentifier() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto result = nodeService.findByIdentifier("NODE-001");

        assertEquals("NODE-001", result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_Null() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(null);
        when(modelMapper.map(null, NodeDto.class)).thenReturn(null);

        assertNull(nodeService.findByIdentifier("NODE-001"));
    }
}