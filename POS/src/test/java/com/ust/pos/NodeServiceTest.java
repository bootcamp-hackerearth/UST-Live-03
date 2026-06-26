package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

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

        Mockito.when(nodeRepository.findByDeletedFalse())
                .thenReturn(List.of(node1, node2));

        Mockito.when(
                nodeRepository.findByIdentifierAndDeletedFalse("NODE1")
        ).thenReturn(node1);

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE1");

        Mockito.when(
                modelMapper.map(node1, NodeDto.class)
        ).thenReturn(nodeDto);

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
    void saveTestSuccess() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node node = new Node();

        Mockito.when(
                nodeRepository.findByIdentifierAndDeletedFalse("N1")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(dto, Node.class)
        ).thenReturn(node);

        NodeDto response = nodeService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(nodeRepository).save(node);

        Assertions.assertFalse(node.getDeleted());
    }

    @Test
    void saveTestFailure() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(
                nodeRepository.findByIdentifierAndDeletedFalse("N1")
        ).thenReturn(new Node());

        NodeDto response = nodeService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Node with identifier - N1 already exists",
                response.getMessage()
        );

        Mockito.verify(
                nodeRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void updateTestSuccess() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node existingNode = new Node();

        Mockito.when(
                nodeRepository.findByIdentifierAndDeletedFalse("N1")
        ).thenReturn(existingNode);

        NodeDto response = nodeService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(modelMapper)
                .map(dto, existingNode);

        Mockito.verify(nodeRepository)
                .save(existingNode);
    }

    @Test
    void updateTestFailure() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(
                nodeRepository.findByIdentifierAndDeletedFalse("N1")
        ).thenReturn(null);

        NodeDto response = nodeService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Node with identifier - N1 not found",
                response.getMessage()
        );

        Mockito.verify(
                nodeRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void findAllTest() {

        List<Node> nodes =
                List.of(new Node(), new Node());

        List<NodeDto> dtoList =
                List.of(new NodeDto(), new NodeDto());

        Mockito.when(
                nodeRepository.findByDeletedFalse()
        ).thenReturn(nodes);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(nodes),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtoList);

        List<NodeDto> response =
                nodeService.findAll();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<Node> nodes =
                List.of(new Node());

        Page<Node> page =
                new PageImpl<>(nodes, pageable, 1);

        List<NodeDto> dtoList =
                List.of(new NodeDto());

        Type listType =
                new TypeToken<List<NodeDto>>() {}.getType();

        Mockito.when(
                nodeRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(nodes, listType)
        ).thenReturn(dtoList);

        WsDto<NodeDto> response =
                nodeService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void findAllSearchTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Node node = new Node();

        Page<Node> page =
                new PageImpl<>(List.of(node));

        Mockito.when(
                nodeRepository
                        .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                                "N1",
                                pageable
                        )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(node, NodeDto.class)
        ).thenReturn(new NodeDto());

        Page<NodeDto> response =
                nodeService.findAll("N1", pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void findByIdentifierTest() {

        Node node = new Node();
        node.setIdentifier("N1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(
                nodeRepository.findByIdentifierAndDeletedFalse("N1")
        ).thenReturn(node);

        Mockito.when(
                modelMapper.map(node, NodeDto.class)
        ).thenReturn(dto);

        NodeDto response =
                nodeService.findByIdentifier("N1");

        Assertions.assertEquals(
                "N1",
                response.getIdentifier()
        );
    }

    @Test
    void deleteTest() {

        Node node = new Node();
        node.setDeleted(false);

        Mockito.when(
                nodeRepository.findByIdentifierAndDeletedFalse("N1")
        ).thenReturn(node);

        nodeService.delete("N1");

        Assertions.assertTrue(node.getDeleted());

        Mockito.verify(nodeRepository)
                .save(node);
    }

    @Test
    void deleteNotFoundTest() {

        Mockito.when(
                nodeRepository.findByIdentifierAndDeletedFalse("N1")
        ).thenReturn(null);

        nodeService.delete("N1");

        Mockito.verify(
                nodeRepository,
                Mockito.never()
        ).save(Mockito.any());
    }
}