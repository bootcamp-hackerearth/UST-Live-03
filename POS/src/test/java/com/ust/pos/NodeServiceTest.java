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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private NodeServiceImpl nodeService;
    private Node sampleNode;
    private NodeDto sampleNodeDto;

    @BeforeEach
    void setUp() {
        sampleNode = new Node();
        sampleNode.setIdentifier("NODE-001");
        sampleNode.setRoles(List.of("ROLE_ADMIN", "ROLE_USER"));
        sampleNodeDto = new NodeDto();
        sampleNodeDto.setIdentifier("NODE-001");
    }

    @Test
    void findByIdentifier_shouldReturnMappedDto() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(sampleNode);
        when(modelMapper.map(sampleNode, NodeDto.class)).thenReturn(sampleNodeDto);
        NodeDto result = nodeService.findByIdentifier("NODE-001");
        assertNotNull(result);
        assertEquals("NODE-001", result.getIdentifier());
        verify(nodeRepository).findByIdentifier("NODE-001");
    }

    @Test
    void findByIdentifier_whenNotFound_shouldReturnNull() {
        when(nodeRepository.findByIdentifier("UNKNOWN")).thenReturn(null);
        when(modelMapper.map(null, NodeDto.class)).thenReturn(null);
        NodeDto result = nodeService.findByIdentifier("UNKNOWN");
        assertNull(result);
    }

    @Test
    void save_whenNodeDoesNotExist_shouldSaveAndReturnDto() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(null);
        when(modelMapper.map(sampleNodeDto, Node.class)).thenReturn(sampleNode);
        NodeDto result = nodeService.save(sampleNodeDto);
        assertNotNull(result);
        verify(nodeRepository).save(sampleNode);
        assertNotEquals(Boolean.FALSE, result.isSuccess());
    }

    @Test
    void save_whenNodeAlreadyExists_shouldReturnFailureDto() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(sampleNode);
        NodeDto result = nodeService.save(sampleNodeDto);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("NODE-001"));
        assertTrue(result.getMessage().contains("already exists"));
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void update_whenNodeExists_shouldUpdateAndReturnDto() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(sampleNode);
        NodeDto result = nodeService.update(sampleNodeDto);
        assertNotNull(result);
        verify(modelMapper).map(sampleNodeDto, sampleNode);
        verify(nodeRepository).save(sampleNode);
    }

    @Test
    void update_whenNodeDoesNotExist_shouldReturnFailureDto() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(null);
        NodeDto result = nodeService.update(sampleNodeDto);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("NODE-001"));
        assertTrue(result.getMessage().contains("not found"));
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void delete_shouldCallRepositoryDeleteByIdentifier() {
        doNothing().when(nodeRepository).deleteByIdentifier("NODE-001");
        nodeService.delete("NODE-001");
        verify(nodeRepository).deleteByIdentifier("NODE-001");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Node node = new Node();
        node.setIdentifier("NODE1");
        List<Node> nodeList = List.of(node);
        Page<Node> nodePage = new PageImpl<>(nodeList);

        Mockito.when(nodeRepository.findAll(pageable)).thenReturn(nodePage);

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE1");
        List<NodeDto> nodeDtoList = List.of(nodeDto);

        Mockito.when(modelMapper.map(Mockito.eq(nodePage.getContent()),Mockito.any(Type.class))).thenReturn(nodeDtoList);

        WsDto<NodeDto> result = nodeService.findAll(pageable);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals("NODE1", result.getDtoList().get(0).getIdentifier());
        Mockito.verify(nodeRepository).findAll(pageable);
    }

    @Test
    void getNodesForRoles_whenAuthenticationIsNull_shouldReturnEmptyList() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);
        List<NodeDto> result = nodeService.getNodesForRoles();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRoles_whenUserHasMatchingRole_shouldReturnMatchingNodes() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User("john", "password", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(principal);

        User domainUser = new User();
        domainUser.setUsername("john");
        domainUser.setRoles(List.of("ROLE_ADMIN"));
        when(userRepository.findByIdentifier("john")).thenReturn(domainUser);
        Node adminNode = new Node();
        adminNode.setIdentifier("NODE-ADMIN");
        adminNode.setRoles(List.of("ROLE_ADMIN"));

        Node userNode = new Node();
        userNode.setIdentifier("NODE-USER");
        userNode.setRoles(List.of("ROLE_USER"));
        when(nodeRepository.findAll()).thenReturn(Arrays.asList(adminNode, userNode));

        NodeDto adminNodeDto = new NodeDto();
        adminNodeDto.setIdentifier("NODE-ADMIN");
        when(nodeRepository.findByIdentifier("NODE-ADMIN")).thenReturn(adminNode);
        when(modelMapper.map(adminNode, NodeDto.class)).thenReturn(adminNodeDto);
        List<NodeDto> result = nodeService.getNodesForRoles();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("NODE-ADMIN", result.get(0).getIdentifier());
    }

    @Test
    void getNodesForRoles_whenUserHasNoMatchingRole_shouldReturnEmptyList() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User("john", "password", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(principal);

        User domainUser = new User();
        domainUser.setUsername("john");
        domainUser.setRoles(List.of("ROLE_CASHIER"));
        when(userRepository.findByIdentifier("john")).thenReturn(domainUser);

        Node adminNode = new Node();
        adminNode.setIdentifier("NODE-ADMIN");
        adminNode.setRoles(List.of("ROLE_ADMIN"));
        when(nodeRepository.findAll()).thenReturn(List.of(adminNode));

        List<NodeDto> result = nodeService.getNodesForRoles();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRoles_whenNodeHasNullRoles_shouldSkipThatNode() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(                 "john", "password", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(principal);

        User domainUser = new User();
        domainUser.setUsername("john");
        domainUser.setRoles(List.of("ROLE_ADMIN"));
        when(userRepository.findByIdentifier("john")).thenReturn(domainUser);

        Node nodeWithNullRoles = new Node();
        nodeWithNullRoles.setIdentifier("NODE-NULL");
        nodeWithNullRoles.setRoles(null);

        when(nodeRepository.findAll()).thenReturn(List.of(nodeWithNullRoles));
        List<NodeDto> result = nodeService.getNodesForRoles();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}