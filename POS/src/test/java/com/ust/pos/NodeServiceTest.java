package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
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
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NodeServiceImpl nodeService;

    private Node testNode;
    private NodeDto testDto;

    @BeforeEach
    void setUp() {
        testNode = new Node();
        testNode.setIdentifier("NODE01");
        testNode.setStatus(true);
        testNode.setRoles(new ArrayList<>(List.of("ROLE_USER")));

        testDto = new NodeDto();
        testDto.setIdentifier("NODE01");
    }

    @Test
    @DisplayName("Save Node - Success")
    void save_Success() {
        when(nodeRepository.findByIdentifier("NODE01")).thenReturn(null);
        when(modelMapper.map(testDto, Node.class)).thenReturn(testNode);

        NodeDto result = nodeService.save(testDto);

        Assertions.assertNotNull(result);
        verify(nodeRepository).save(testNode);
    }

    @Test
    @DisplayName("Save Node - Already Exists")
    void save_AlreadyExists() {
        when(nodeRepository.findByIdentifier("NODE01")).thenReturn(testNode);

        NodeDto result = nodeService.save(testDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Node with identifier - NODE01 already exists", result.getMessage());
        verify(nodeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update Node - Success")
    void update_Success() {
        when(nodeRepository.findByIdentifier("NODE01")).thenReturn(testNode);

        NodeDto result = nodeService.update(testDto);

        Assertions.assertNotNull(result);
        verify(modelMapper).map(testDto, testNode);
        verify(nodeRepository).save(testNode);
    }

    @Test
    @DisplayName("Update Node - Not Found")
    void update_NotFound() {
        when(nodeRepository.findByIdentifier("NODE01")).thenReturn(null);

        NodeDto result = nodeService.update(testDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Node with identifier - NODE01 not found", result.getMessage());
        verify(nodeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Toggle Status - Toggle and Save")
    void toggleStatus_Test() {
        when(nodeRepository.findByIdentifier("NODE01")).thenReturn(testNode);
        when(modelMapper.map(testNode, NodeDto.class)).thenReturn(testDto);

        NodeDto result = nodeService.toggleStatus("NODE01");

        Assertions.assertFalse(testNode.isStatus());
        verify(nodeRepository).save(testNode);
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Find All - Paginated Mapping into WsDto")
    void findAll_Paginated_Test() {
        Pageable pageable = PageRequest.of(1, 10);
        List<Node> nodes = List.of(testNode);
        Page<Node> nodePage = new PageImpl<>(nodes, pageable, 45);
        List<NodeDto> mappedDtos = List.of(testDto);

        when(nodeRepository.findAll(pageable)).thenReturn(nodePage);
        when(modelMapper.map(eq(nodes), any(Type.class))).thenReturn(mappedDtos);

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(mappedDtos, result.getDtoList());
        Assertions.assertEquals(45, result.getTotalRecords());
        Assertions.assertEquals(5, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(1, result.getPage());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Test() {
        when(nodeRepository.findByIdentifier("NODE01")).thenReturn(testNode);
        when(modelMapper.map(testNode, NodeDto.class)).thenReturn(testDto);

        NodeDto result = nodeService.findByIdentifier("NODE01");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("NODE01", result.getIdentifier());
    }

    @Test
    @DisplayName("Get Nodes For Roles - Authenticated with Matching Roles")
    void getNodesForRoles_Success() {
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("testUser", "pass", Collections.emptyList());

        User currentUser = new User();
        currentUser.setRoles(new ArrayList<>(List.of("ROLE_USER")));

        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(auth);
            when(auth.getPrincipal()).thenReturn(principal);

            when(userRepository.findByUsername("testUser")).thenReturn(currentUser);

            Node matchingNode = new Node();
            matchingNode.setIdentifier("MATCH01");
            matchingNode.setRoles(List.of("ROLE_USER"));
            matchingNode.setStatus(true);

            when(nodeRepository.findAllByStatus(true)).thenReturn(List.of(matchingNode));
            when(nodeRepository.findByIdentifier("MATCH01")).thenReturn(matchingNode);
            when(modelMapper.map(matchingNode, NodeDto.class)).thenReturn(new NodeDto());

            List<NodeDto> result = nodeService.getNodesForRoles();

            Assertions.assertFalse(result.isEmpty());
            verify(nodeRepository).findAllByStatus(true);
            verify(nodeRepository).findByIdentifier("MATCH01");
        }
    }

    @Test
    @DisplayName("Get Nodes For Roles - Unauthenticated Request")
    void getNodesForRoles_AuthNull() {
        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            List<NodeDto> result = nodeService.getNodesForRoles();
            Assertions.assertTrue(result.isEmpty());
            verifyNoInteractions(userRepository, nodeRepository);
        }
    }

    @Test
    @DisplayName("Delete Node - Success")
    void delete_Test() {
        boolean result = nodeService.delete("NODE01");
        Assertions.assertTrue(result);
        verify(nodeRepository).deleteByIdentifier("NODE01");
    }
}