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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @InjectMocks
    private NodeServiceImpl nodeService;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void constructorTest() {
        NodeServiceImpl service = new NodeServiceImpl(userRepository, nodeRepository, modelMapper);
        Assertions.assertNotNull(service);
    }

    @Test
    void getNodesForRolesTest() {
        Authentication authentication = Mockito.mock(Authentication.class);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);

        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User("user", "password", List.of());

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        User user = new User();
        user.setRoles(List.of("ADMIN"));
        Node node = new Node();
        node.setIdentifier("NODE1");
        node.setRoles(List.of("ADMIN"));
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Mockito.when(userRepository.findByUsername("user")).thenReturn(user);
        Mockito.when(nodeRepository.findByIsDeletedFalse()).thenReturn(List.of(node));
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("NODE1", result.get(0).getIdentifier());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Node node = new Node();
        node.setIdentifier("N1");
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifierAndIsDeletedFalse("N1")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        NodeDto result = nodeService.findByIdentifier("N1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("N1", result.getIdentifier());
        Mockito.verify(nodeRepository).findByIdentifierAndIsDeletedFalse("N1");
    }

    @Test
    void findByIdentifierFailureTest() {
        Mockito.when(nodeRepository.findByIdentifierAndIsDeletedFalse("N1")).thenReturn(null);

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> nodeService.findByIdentifier("N1"));

        Mockito.verify(nodeRepository).findByIdentifierAndIsDeletedFalse("N1");
    }

    @Test
    void saveSuccessTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node node = new Node();

        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Node.class)).thenReturn(node);

        NodeDto result = nodeService.save(dto);

        Assertions.assertEquals("N1", result.getIdentifier());

        Mockito.verify(nodeRepository).save(node);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        Node existing = new Node();
        existing.setIdentifier("N1");
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(existing);

        NodeDto result = nodeService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Node with identifier - N1 already exists", result.getMessage());
    }

    @Test
    void saveFailureDeletedIdentifierTest() {
        Node existing = new Node();
        existing.setIdentifier("N1");
        existing.setDeleted(true);
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(existing);

        NodeDto result = nodeService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Node identifier - N1 not available", result.getMessage());
    }

    @Test
    void updateSuccessTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Node node = new Node();

        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(node);

        NodeDto result = nodeService.update(dto);

        Assertions.assertEquals("N1", result.getIdentifier());

        Mockito.verify(modelMapper).map(dto, node);
        Mockito.verify(nodeRepository).save(node);
    }

    @Test
    void updateFailureTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(null);

        NodeDto result = nodeService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Node with identifier - N1 not found", result.getMessage());
    }

    @Test
    void deleteTest() {
        Node node = new Node();
        node.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(node);

        nodeService.delete("N1");

        Assertions.assertTrue(node.isDeleted());
    }

    @Test
    void findAllTest() {
        Node node = new Node();
        node.setIdentifier("N1");
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        List<Node> nodes = List.of(node);
        List<NodeDto> dtoList = List.of(dto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Node> page = new PageImpl<>(nodes, pageable, nodes.size());

        Mockito.when(nodeRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(nodes), Mockito.any(Type.class))).thenReturn(dtoList);

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());

        Mockito.verify(nodeRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Node node = new Node();
        node.setIdentifier("N1");
        List<Node> nodes = List.of(node);

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        List<NodeDto> dtoList = List.of(dto);

        Page<Node> page = new PageImpl<>(nodes, pageable, nodes.size());

        @SuppressWarnings("unchecked")
        Specification<Node> specification = Mockito.mock(Specification.class);

        Mockito.when(nodeRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(nodes), Mockito.any(Type.class))).thenReturn(dtoList);

        WsDto<NodeDto> result = nodeService.findAll(specification, pageable, "node");

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals("node", result.getKeyword());

        Mockito.verify(nodeRepository).findAll(specification, pageable);
    }

    @Test
    void getNodesForRolesAuthenticationNullTest() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);

        Mockito.when(securityContext.getAuthentication()).thenReturn(null);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRolesInvalidPrincipalTest() {
        Authentication authentication = Mockito.mock(Authentication.class);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("anonymousUser");

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRolesUserNotFoundTest() {
        Authentication authentication = Mockito.mock(Authentication.class);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);

        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User("user", "password", List.of());

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);
        Mockito.when(userRepository.findByUsername("user")).thenReturn(null);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRolesNoMatchingRoleTest() {
        Authentication authentication = Mockito.mock(Authentication.class);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);

        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User("user", "password", List.of());

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        User user = new User();
        user.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setIdentifier("NODE1");
        node.setRoles(List.of("USER"));

        Mockito.when(userRepository.findByUsername("user")).thenReturn(user);
        Mockito.when(nodeRepository.findByIsDeletedFalse()).thenReturn(List.of(node));

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }
}