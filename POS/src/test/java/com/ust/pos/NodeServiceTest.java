package com.ust.pos;

import com.ust.pos.dto.NodeDto;
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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    // ---------------- GET NODES FOR ROLES ----------------

    @Test
    void getNodesForRolesTest() {

        UserDetails springUser =
                new org.springframework.security.core.userdetails.User(
                        "admin",
                        "pwd",
                        new ArrayList<>());

        Mockito.when(authentication.getPrincipal())
                .thenReturn(springUser);

        Mockito.when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setUsername("admin");
        user.setRoles(List.of("ADMIN"));

        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(user);

        Node node1 = new Node();
        node1.setIdentifier("NODE1");
        node1.setRoles(List.of("ADMIN"));

        Node node2 = new Node();
        node2.setIdentifier("NODE2");
        node2.setRoles(List.of("USER"));

        Mockito.when(nodeRepository.findAll())
                .thenReturn(Arrays.asList(node1, node2));

        Mockito.when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node1);

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE1");

        Mockito.when(modelMapper.map(node1, NodeDto.class))
                .thenReturn(nodeDto);

        List<NodeDto> result =
                nodeService.getNodesForRoles();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(
                "NODE1",
                result.get(0).getIdentifier()
        );

        SecurityContextHolder.clearContext();
    }

    @Test
    void getNodesForRoles_WhenNoMatchingRoles_ShouldReturnEmptyList() {

        UserDetails springUser =
                new org.springframework.security.core.userdetails.User(
                        "admin",
                        "pwd",
                        new ArrayList<>());

        Mockito.when(authentication.getPrincipal())
                .thenReturn(springUser);

        Mockito.when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setUsername("admin");
        user.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setIdentifier("NODE1");
        node.setRoles(List.of("USER"));

        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(user);

        Mockito.when(nodeRepository.findAll())
                .thenReturn(List.of(node));

        List<NodeDto> result =
                nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());

        SecurityContextHolder.clearContext();
    }

    // ---------------- SAVE ----------------

    @Test
    void saveTest_Success() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node entity = new Node();

        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Node.class))
                .thenReturn(entity);

        Mockito.when(nodeRepository.save(entity))
                .thenReturn(entity);

        NodeDto response = nodeService.save(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "N1",
                response.getIdentifier()
        );

        Mockito.verify(nodeRepository)
                .save(entity);
    }

    @Test
    void saveTest_Failure_WhenExists() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(new Node());

        NodeDto response = nodeService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(nodeRepository, Mockito.never())
                .save(Mockito.any());
    }

    // ---------------- UPDATE ----------------

    @Test
    void updateTest_Success() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node existing = new Node();
        existing.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper)
                .map(dto, existing);

        Mockito.when(nodeRepository.save(existing))
                .thenReturn(existing);

        NodeDto response = nodeService.update(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "N1",
                response.getIdentifier()
        );

        Mockito.verify(nodeRepository)
                .save(existing);
    }

    @Test
    void updateTest_Failure_WhenNotFound() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(null);

        NodeDto response = nodeService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(nodeRepository, Mockito.never())
                .save(Mockito.any());
    }

    // ---------------- FIND ALL ----------------

    @Test
    void findAllTest() {

        List<Node> entities =
                List.of(new Node());

        List<NodeDto> dtos =
                List.of(new NodeDto());

        Type listType =
                new TypeToken<List<NodeDto>>() {
                }.getType();

        Mockito.when(nodeRepository.findAll())
                .thenReturn(entities);

        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);

        List<NodeDto> response =
                nodeService.findAll();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
    }

    // ---------------- FIND BY IDENTIFIER ----------------

    @Test
    void findByIdentifierTest() {

        Node node = new Node();
        node.setIdentifier("N1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(node);

        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        NodeDto response =
                nodeService.findByIdentifier("N1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "N1",
                response.getIdentifier()
        );
    }

    @Test
    void findByIdentifier_WhenNodeNotFound() {

        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(null, NodeDto.class))
                .thenReturn(null);

        NodeDto response =
                nodeService.findByIdentifier("N1");

        Assertions.assertNull(response);
    }

    // ---------------- DELETE ----------------

    @Test
    void deleteTest() {

        Mockito.doNothing()
                .when(nodeRepository)
                .deleteByIdentifier("N1");

        nodeService.delete("N1");

        Mockito.verify(nodeRepository)
                .deleteByIdentifier("N1");
    }

    // ---------------- PAGINATION WITHOUT SEARCH ----------------

    @Test
    void findAll_WithPagination_ShouldReturnNodeDtos() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Node node = new Node();
        node.setIdentifier("NODE1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Page<Node> nodePage =
                new PageImpl<>(List.of(node));

        Mockito.when(nodeRepository.findAll(pageable))
                .thenReturn(nodePage);

        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        Page<NodeDto> response =
                nodeService.findAll(pageable, null);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );

        Assertions.assertEquals(
                "NODE1",
                response.getContent()
                        .get(0)
                        .getIdentifier()
        );

        Mockito.verify(nodeRepository)
                .findAll(pageable);

        Mockito.verify(modelMapper)
                .map(node, NodeDto.class);
    }

    // ---------------- PAGINATION WITH SEARCH ----------------

    @Test
    void findAll_WithSearch_ShouldReturnNodeDtos() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Node node = new Node();
        node.setIdentifier("NODE1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Page<Node> nodePage =
                new PageImpl<>(List.of(node));

        Mockito.when(
                        nodeRepository
                                .findByIdentifierContainingIgnoreCase(
                                        "NODE",
                                        pageable))
                .thenReturn(nodePage);

        Mockito.when(
                        modelMapper.map(
                                node,
                                NodeDto.class))
                .thenReturn(dto);

        Page<NodeDto> response =
                nodeService.findAll(
                        pageable,
                        "NODE");

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );

        Assertions.assertEquals(
                "NODE1",
                response.getContent()
                        .get(0)
                        .getIdentifier()
        );

        Mockito.verify(nodeRepository)
                .findByIdentifierContainingIgnoreCase(
                        "NODE",
                        pageable);

        Mockito.verify(modelMapper)
                .map(node, NodeDto.class);
    }

    // ---------------- PAGINATION WITH BLANK SEARCH ----------------

    @Test
    void findAll_WithBlankSearch_ShouldUseFindAll() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Node node = new Node();
        node.setIdentifier("NODE1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Page<Node> nodePage =
                new PageImpl<>(List.of(node));

        Mockito.when(nodeRepository.findAll(pageable))
                .thenReturn(nodePage);

        Mockito.when(
                        modelMapper.map(
                                node,
                                NodeDto.class))
                .thenReturn(dto);

        Page<NodeDto> response =
                nodeService.findAll(
                        pageable,
                        " ");

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );

        Mockito.verify(nodeRepository)
                .findAll(pageable);

        Mockito.verify(modelMapper)
                .map(node, NodeDto.class);
    }
}