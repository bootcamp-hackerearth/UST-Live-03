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
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private NodeServiceImpl nodeService;

    private Node node;
    private NodeDto nodeDto;

    @BeforeEach
    void setUp() {

        node = new Node();
        node.setIdentifier("NODE1");
        node.setStatus(true);
        node.setDeleted(false);
        node.setRoles(List.of("ROLE_ADMIN"));

        nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE1");
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testFindByIdentifier() {

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(nodeDto);

        NodeDto result = nodeService.findByIdentifier("NODE1");

        assertNotNull(result);
        assertEquals("NODE1", result.getIdentifier());
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Node> page =
                new PageImpl<>(Collections.singletonList(node));

        when(nodeRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(nodeDto));

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void testFindAllWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Node> specification = mock(Specification.class);

        Page<Node> page =
                new PageImpl<>(Collections.singletonList(node));

        when(nodeRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(nodeDto));

        WsDto<NodeDto> result =
                nodeService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());

        verify(nodeRepository)
                .findAll(specification, pageable);
    }

    @Test
    void testSave_NewNode() {

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(null);

        when(modelMapper.map(nodeDto, Node.class))
                .thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        assertNotNull(result);

        verify(nodeRepository).save(node);
    }

    @Test
    void testSave_AlreadyExists() {

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_SoftDeleted() {

        node.setDeleted(true);

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    @Test
    void testUpdate_Success() {

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node);

        doNothing().when(modelMapper)
                .map(nodeDto, node);

        NodeDto result = nodeService.update(nodeDto);

        assertNotNull(result);

        verify(nodeRepository).save(node);
    }

    @Test
    void testUpdate_NotFound() {

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(null);

        NodeDto result = nodeService.update(nodeDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testDelete() {

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node);

        nodeService.delete("NODE1");

        assertTrue(node.isDeleted());
        assertFalse(node.isStatus());

        verify(nodeRepository).save(node);
    }

    @Test
    void testGetNodesForRoles() {

        User user = new User();
        user.setUsername("admin");
        user.setRoles(List.of("ROLE_ADMIN"));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new org.springframework.security.core.userdetails.User(
                                "admin",
                                "password",
                                List.of()
                        ),
                        null,
                        List.of()
                )
        );

        when(userRepository.findByUsername("admin"))
                .thenReturn(user);

        when(nodeRepository.findAll())
                .thenReturn(List.of(node));

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(nodeDto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetNodesForRoles_NoAuthentication() {

        SecurityContextHolder.clearContext();

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testChangeToggleStatus() {

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(nodeDto);

        NodeDto result =
                nodeService.changeToggleStatus("NODE1", false);

        assertNotNull(result);
        assertFalse(node.isStatus());

        verify(nodeRepository).save(node);
    }

    @Test
    void testFindActiveStatus() {

        Node inactiveNode = new Node();
        inactiveNode.setStatus(false);

        when(nodeRepository.findAll())
                .thenReturn(List.of(node, inactiveNode));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(nodeDto));

        List<NodeDto> result = nodeService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}