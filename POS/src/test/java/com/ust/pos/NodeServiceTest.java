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

    @Test
    void findAllPageableWithSearchTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Node node = new Node();
        node.setIdentifier("NODE1");
        Page<Node> page =
                new PageImpl<>(List.of(node));
        Mockito.when(
                nodeRepository
                        .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                                "NODE",
                                pageable
                        )
        ).thenReturn(page);
        Page<NodeDto> result =
                nodeService.findAll(pageable, "NODE");
        Assertions.assertEquals(
                1,
                result.getContent().size()
        );
        Mockito.verify(nodeRepository)
                .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                        "NODE",
                        pageable
                );
    }

    @Test
    void findAllPageableWithoutSearchTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Node node = new Node();
        node.setIdentifier("NODE1");
        Page<Node> page =
                new PageImpl<>(List.of(node));
        Mockito.when(
                nodeRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);
        Page<NodeDto> result =
                nodeService.findAll(pageable, null);
        Assertions.assertEquals(
                1,
                result.getContent().size()
        );
        Mockito.verify(nodeRepository)
                .findByDeletedFalse(pageable);
    }

    @Test
    void getNodesForRolesTest() {
        UserDetails springUser =
                new org.springframework.security.core.userdetails.User(
                        "admin", "pwd", new ArrayList<>());
        Mockito.when(authentication.getPrincipal()).thenReturn(springUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User user = new User();
        user.setUsername("admin");
        List<String> roles = new ArrayList<>();
        roles.add("ADMIN");
        user.setRoles(roles);
        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(user);
        Node node1 = new Node();
        node1.setIdentifier("NODE1");
        node1.setRoles(Arrays.asList("ADMIN"));
        Node node2 = new Node();
        node2.setIdentifier("NODE2");
        node2.setRoles(Arrays.asList("USER"));
        Mockito.when(nodeRepository.findByDeletedFalse())
                .thenReturn(Arrays.asList(node1, node2));
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("NODE1"))
                .thenReturn(node1);
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE1");
        Mockito.when(modelMapper.map(node1, NodeDto.class))
                .thenReturn(nodeDto);
        List<NodeDto> result = nodeService.getNodesForRoles();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("NODE1", result.get(0).getIdentifier());
        SecurityContextHolder.clearContext();
    }

    @Test
    void saveTest_Success() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");
        Node node = new Node();
        Mockito.when(
                nodeRepository.findByIdentifierAndDeletedFalse("NODE1")
        ).thenReturn(null);
        Mockito.when(
                modelMapper.map(dto, Node.class)
        ).thenReturn(node);
        nodeService.save(dto);
        Mockito.verify(nodeRepository)
                .save(node);
    }

    @Test
    void saveTest_Failure() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");
        Mockito.when(
                nodeRepository.findByIdentifierAndDeletedFalse("NODE1")
        ).thenReturn(new Node());
        NodeDto response =
                nodeService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Mockito.verify(
                nodeRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void updateTest_Success() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");
        Node existingNode = new Node();
        Mockito.when(
                nodeRepository.findByIdentifierAndDeletedFalse("NODE1")
        ).thenReturn(existingNode);
        Mockito.doNothing()
                .when(modelMapper)
                .map(dto, existingNode);
        nodeService.update(dto);
        Mockito.verify(modelMapper)
                .map(dto, existingNode);
        Mockito.verify(nodeRepository)
                .save(existingNode);
    }

    @Test
    void updateTest_Failure() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");
        Mockito.when(
                nodeRepository.findByIdentifierAndDeletedFalse("NODE1")
        ).thenReturn(null);
        NodeDto response =
                nodeService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Mockito.verify(
                nodeRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void findAllTest() {
        List<Node> entities = List.of(new Node());
        List<NodeDto> dtos = List.of(new NodeDto());
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        Mockito.when(nodeRepository.findByDeletedFalse())
                .thenReturn(entities);
        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);
        List<NodeDto> response = nodeService.findAll();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByIdentifierTest() {
        Node node = new Node();
        node.setIdentifier("N1");
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1"))
                .thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);
        NodeDto response = nodeService.findByIdentifier("N1");
        Assertions.assertEquals("N1", response.getIdentifier());
    }

    @Test
    void deleteTest() {
        Node node = new Node();
        node.setDeleted(false);
        Mockito.when(
                nodeRepository.findByIdentifierAndDeletedFalse("NODE1")
        ).thenReturn(node);
        Mockito.when(
                nodeRepository.save(node)
        ).thenReturn(node);
        nodeService.delete("NODE1");
        Assertions.assertTrue(node.isDeleted());
        Mockito.verify(nodeRepository)
                .save(node);
    }

    @Test
    void deleteNotFoundTest() {

        Mockito.when(
                nodeRepository.findByIdentifierAndDeletedFalse("NODE1")
        ).thenReturn(null);

        nodeService.delete("NODE1");

        Mockito.verify(
                nodeRepository,
                Mockito.never()
        ).save(Mockito.any());
    }
}