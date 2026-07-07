package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private NodeServiceImpl nodeService;

    private NodeDto nodeDto;
    private Node node;
    private User user;

    @BeforeEach
    void setUp() {
        nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE-001");

        node = new Node();
        node.setIdentifier("NODE-001");
        node.setStatus(true);
        node.setDeleted(false);
        node.setRoles(List.of("ROLE_ADMIN"));

        user = new User();
        user.setUsername("adminUser");
        user.setRoles(List.of("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Save Node - Success")
    void save_Success() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(null);
        when(modelMapper.map(nodeDto, Node.class)).thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("successfully"));
        verify(nodeRepository).save(node);
    }

    @Test
    @DisplayName("Save Node - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        node.setDeleted(false);
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(nodeRepository, never()).save(any(Node.class));
    }

    @Test
    @DisplayName("Save Node - Failure: Previously Soft-Deleted")
    void save_Failure_PreviouslyDeleted() {
        node.setDeleted(true);
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("previously deleted"));
        verify(nodeRepository, never()).save(any(Node.class));
    }

    @Test
    @DisplayName("Find All Nodes - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Node> nodePage = new PageImpl<>(List.of(node), pageable, 1);

        when(nodeRepository.findByDeletedFalse(pageable)).thenReturn(nodePage);
        when(modelMapper.map(eq(nodePage.getContent()), any(Type.class))).thenReturn(List.of(nodeDto));

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    @DisplayName("Find All Nodes with Specification - Success")
    void findAll_WithSpecification_Success() {
        Specification<Node> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Node> nodePage = new PageImpl<>(List.of(node), pageable, 1);

        when(nodeRepository.findAll(spec, pageable)).thenReturn(nodePage);
        when(modelMapper.map(eq(nodePage.getContent()), any(Type.class))).thenReturn(List.of(nodeDto));

        WsDto<NodeDto> result = nodeService.findAll(spec, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto result = nodeService.findByIdentifier("NODE-001");

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Find By Identifier - Failure: Not Found Exception")
    void findByIdentifier_Failure_NotFound() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> nodeService.findByIdentifier("NODE-001"));
    }

    @Test
    @DisplayName("Get Nodes For Roles - Success")
    void getNodesForRoles_Success() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("adminUser", "password", Collections.emptyList());

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(userRepository.findByUsername("adminUser")).thenReturn(user);
        when(nodeRepository.findAllByStatusAndDeletedFalse(true)).thenReturn(List.of(node));
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Update Node - Success")
    void update_Success() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);

        NodeDto result = nodeService.update(nodeDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("successfully"));
        verify(nodeRepository).save(node);
        verify(modelMapper).map(nodeDto, node);
    }

    @Test
    @DisplayName("Update Node - Failure: Not Found")
    void update_Failure_NotFound() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(null);

        NodeDto result = nodeService.update(nodeDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(nodeRepository, never()).save(any(Node.class));
    }

    @Test
    @DisplayName("Update Node - Failure: Already Deleted")
    void update_Failure_AlreadyDeleted() {
        node.setDeleted(true);
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);

        NodeDto result = nodeService.update(nodeDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("previously deleted"));
        verify(nodeRepository, never()).save(any(Node.class));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto result = nodeService.toggleStatus("NODE-001");

        Assertions.assertFalse(node.isStatus());
        verify(nodeRepository).save(node);
    }

    @Test
    @DisplayName("Delete Node - Success")
    void delete_Success() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);

        boolean result = nodeService.delete("NODE-001");

        Assertions.assertTrue(result);
        verify(nodeRepository).save(node);
    }

    @Test
    @DisplayName("Delete Node - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(null);

        boolean result = nodeService.delete("NODE-001");

        Assertions.assertFalse(result);
        verify(nodeRepository, never()).save(any(Node.class));
    }
}