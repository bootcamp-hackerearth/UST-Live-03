package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationResponseDto;
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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
    void findByIdentifierTest() {
        Node node = new Node();
        node.setIdentifier("NODE001");
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");
        Mockito.when(nodeRepository.findByIdentifier("NODE001")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);
        NodeDto response = nodeService.findByIdentifier("NODE001");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("NODE001", response.getIdentifier());
    }

    @Test
    void saveTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");
        Node node = new Node();
        Mockito.when(nodeRepository.findByIdentifier("NODE001")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Node.class)).thenReturn(node);
        Mockito.when(nodeRepository.save(node)).thenReturn(node);
        NodeDto response = nodeService.save(dto);
        Assertions.assertEquals("NODE001", response.getIdentifier());
        Mockito.verify(nodeRepository).save(node);
    }

    @Test
    void saveDuplicateTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");
        Node existing = new Node();
        Mockito.when(nodeRepository.findByIdentifier("NODE001")).thenReturn(existing);
        NodeDto response = nodeService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(nodeRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveSoftDeletedNodeTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");
        Node existing = new Node();
        existing.setDeleted(true);
        Mockito.when(nodeRepository.findByIdentifier("NODE001"))
                .thenReturn(existing);
        NodeDto response = nodeService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void updateTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");
        Node existing = new Node();
        Mockito.when(nodeRepository.findByIdentifier("NODE001")).thenReturn(existing);
        Mockito.when(nodeRepository.save(existing)).thenReturn(existing);
        NodeDto response = nodeService.update(dto);
        Assertions.assertNotNull(response);
        Mockito.verify(nodeRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");
        Mockito.when(nodeRepository.findByIdentifier("NODE001")).thenReturn(null);
        NodeDto response = nodeService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(nodeRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Node node = new Node();
        node.setIdentifier("NODE001");
        Mockito.when(nodeRepository.findByIdentifier("NODE001")).thenReturn(node);
        nodeService.delete("NODE001");
        Assertions.assertTrue(node.isDeleted());
        Mockito.verify(nodeRepository).save(node);
    }

    @Test
    void deleteNotFoundTest() {
        Mockito.when(nodeRepository.findByIdentifier("NODE001")).thenReturn(null);
        Assertions.assertThrows(RuntimeException.class, () -> nodeService.delete("NODE001"));
    }

    @Test
    void findAllWithPageableTest() {
        Node node = new Node();
        node.setIdentifier("NODE001");
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");
        List<Node> nodes = List.of(node);
        List<NodeDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Node> page = new PageImpl<>(nodes);
        Mockito.when(nodeRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(nodes), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<NodeDto> response = nodeService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("NODE001", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Node node = new Node();
        node.setIdentifier("NODE001");
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");
        List<Node> nodes = List.of(node);
        List<NodeDto> dtos = List.of(dto);
        Mockito.when(nodeRepository.findAll()).thenReturn(nodes);
        Mockito.when(modelMapper.map(Mockito.eq(nodes), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<NodeDto> response = nodeService.findAll(null);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("NODE001", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void getNodesForRolesTest() {
        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User
                        ("admin", "password", new ArrayList<>());
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(springUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = new User();
        user.setUsername("admin");
        user.setRoles(List.of("ROLE_ADMIN"));
        Mockito.when(userRepository.findByUsername("admin")).thenReturn(user);
        Node node = new Node();
        node.setIdentifier("dashboard");
        node.setRoles(List.of("ROLE_ADMIN"));
        Mockito.when(nodeRepository.findAll()).thenReturn(List.of(node));
        Mockito.when(nodeRepository.findByIdentifier("dashboard")).thenReturn(node);
        NodeDto dto = new NodeDto();
        dto.setIdentifier("dashboard");
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);
        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("dashboard", response.get(0).getIdentifier());
    }

    @Test
    void getNodesForRolesAnonymousUserTest() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("anonymousUser");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Assertions.assertThrows(ClassCastException.class, () -> nodeService.getNodesForRoles());
    }

    @Test
    void findAllWithSpecificationTest() {
        Node node = new Node();
        node.setIdentifier("NODE001");
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");
        List<Node> nodes = List.of(node);
        List<NodeDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Node> page = new PageImpl<>(nodes, pageable, 1);
        Specification<Node> specification = Mockito.mock(Specification.class);
        Mockito.when(nodeRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(nodes), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<NodeDto> response = nodeService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("NODE001", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(5, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(nodeRepository).findAll(specification, pageable);
    }

    @Test
    void findAllWithSpecificationNoDataTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Specification<Node> specification = Mockito.mock(Specification.class);
        Page<Node> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        Mockito.when(nodeRepository.findAll(specification, pageable)).thenReturn(emptyPage);
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());
        PaginationResponseDto<NodeDto> response = nodeService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getDtoList().isEmpty());
        Assertions.assertEquals(0, response.getTotalRecords());
        Assertions.assertEquals(0, response.getTotalPages());
        Assertions.assertEquals(5, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(nodeRepository).findAll(specification, pageable);
    }
}