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
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @Test
    void saveTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        Node node = new Node();
        Mockito.when(modelMapper.map(dto, Node.class)).thenReturn(node);
        Mockito.when(nodeRepository.save(node)).thenReturn(node);
        NodeDto response = nodeService.save(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestAlreadyExists() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Node existing = new Node();
        existing.setDeleted(false);
        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(existing);
        NodeDto response = nodeService.save(dto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveTestDeletedExists() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Node existing = new Node();
        existing.setDeleted(true);
        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(existing);
        NodeDto response = nodeService.save(dto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Node existing = new Node();
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(nodeRepository.save(existing)).thenReturn(existing);
        NodeDto response = nodeService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1")).thenReturn(null);
        NodeDto response = nodeService.update(dto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Node node = new Node();
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1")).thenReturn(node);
        Mockito.when(nodeRepository.save(node)).thenReturn(node);
        nodeService.delete("N1");
        Mockito.verify(nodeRepository).save(node);
    }

    @Test
    void deleteNullTest() {
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1")).thenReturn(null);
        nodeService.delete("N1");
        Mockito.verify(nodeRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllTest() {
        Node node = new Node();
        NodeDto dto = new NodeDto();
        List<Node> list = List.of(node);
        List<NodeDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Node> page = new PageImpl<>(list);
        Mockito.when(nodeRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        WsDto<NodeDto> response = nodeService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void findByIdentifierTest() {
        Node node = new Node();
        NodeDto dto = new NodeDto();
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);
        NodeDto response = nodeService.findByIdentifier("N1");
        Assertions.assertNotNull(response);
    }

    @Test
    void getNodesForRolesTest() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("admin@test.com", "pass", List.of());
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getPrincipal()).thenReturn(principal);
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        User user = new User();
        user.setRoles(List.of("ADMIN"));
        Mockito.when(userRepository.findByUsername("admin@test.com")).thenReturn(user);
        Node node = new Node();
        node.setIdentifier("dashboard");
        node.setRoles(List.of("ADMIN"));
        Page<Node> page = new PageImpl<>(List.of(node));
        Mockito.when(nodeRepository.findByDeletedFalse(Pageable.unpaged())).thenReturn(page);
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("dashboard")).thenReturn(node);
        NodeDto dto = new NodeDto();
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);
        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void getNodesForRolesNoAuthTest() {
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(context);
        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRolesNullPrincipalTest() {
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getPrincipal()).thenReturn(null);
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRolesNoMatchTest() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("user@test.com", "pass", List.of());
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getPrincipal()).thenReturn(principal);
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        User user = new User();
        user.setRoles(List.of("USER"));
        Mockito.when(userRepository.findByUsername("user@test.com")).thenReturn(user);
        Node node = new Node();
        node.setRoles(List.of("ADMIN"));
        Page<Node> page = new PageImpl<>(List.of(node));
        Mockito.when(nodeRepository.findByDeletedFalse(Pageable.unpaged())).thenReturn(page);
        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRolesNullRolesTest() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("user@test.com", "pass", List.of());
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getPrincipal()).thenReturn(principal);
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        User user = new User();
        user.setRoles(List.of("ADMIN"));
        Mockito.when(userRepository.findByUsername("user@test.com")).thenReturn(user);
        Node node = new Node();
        node.setRoles(null);
        Page<Node> page = new PageImpl<>(List.of(node));
        Mockito.when(nodeRepository.findByDeletedFalse(Pageable.unpaged())).thenReturn(page);
        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Node> page = new PageImpl<>(List.of(new Node()), pageable, 1);
        Mockito.when(nodeRepository.findAll(
                        Mockito.<org.springframework.data.jpa.domain.Specification<Node>>any(),
                        Mockito.eq(pageable)))
                .thenReturn(page);
        List<NodeDto> dtoList = List.of(new NodeDto());
        Mockito.when(modelMapper.map(
                        Mockito.eq(page.getContent()),
                        Mockito.any(Type.class)))
                .thenReturn(dtoList);
        WsDto<NodeDto> result =
                nodeService.findAll(Mockito.mock(org.springframework.data.jpa.domain.Specification.class), pageable);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1"))
                .thenReturn(null);
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> nodeService.findByIdentifier("N1")
        );
    }
}