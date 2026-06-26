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
        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        "admin",
                        "password",
                        new ArrayList<>()
                );

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

        Node node = new Node();
        node.setIdentifier("NODE1");
        node.setRoles(List.of("ADMIN"));

        Mockito.when(nodeRepository.findByDeletedFalse())
                .thenReturn(List.of(node));

        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("NODE1"))
                .thenReturn(node);

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        List<NodeDto> response = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(
                "NODE1",
                response.get(0).getIdentifier()
        );

        SecurityContextHolder.clearContext();
    }

    @Test
    void saveTestSuccess() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node node = new Node();

        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Node.class))
                .thenReturn(node);

        Mockito.when(nodeRepository.save(node))
                .thenReturn(node);

        NodeDto response = nodeService.save(dto);

        Assertions.assertEquals(
                "N1",
                response.getIdentifier()
        );

        Mockito.verify(nodeRepository)
                .save(node);
    }

    @Test
    void saveTestFailureWhenExists() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1"))
                .thenReturn(new Node());

        NodeDto response = nodeService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(nodeRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTestSuccess() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node existing = new Node();

        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper)
                .map(dto, existing);

        NodeDto response = nodeService.update(dto);

        Assertions.assertEquals(
                "N1",
                response.getIdentifier()
        );

        Mockito.verify(nodeRepository)
                .save(existing);
    }

    @Test
    void updateTestFailureWhenNotFound() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1"))
                .thenReturn(null);

        NodeDto response = nodeService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(nodeRepository, Mockito.never())
                .save(Mockito.any());
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

        Assertions.assertEquals(
                "N1",
                response.getIdentifier()
        );
    }

    @Test
    void findByIdentifierNullTest() {
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(null, NodeDto.class))
                .thenReturn(null);

        NodeDto response = nodeService.findByIdentifier("N1");

        Assertions.assertNull(response);
    }

    @Test
    void deleteTest() {
        Node node = new Node();
        node.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1"))
                .thenReturn(node);

        nodeService.delete("N1");

        Assertions.assertTrue(node.isDeleted());

        Mockito.verify(nodeRepository)
                .save(node);
    }

    @Test
    void findAllTest() {
        List<Node> nodes = List.of(new Node());
        List<NodeDto> dtos = List.of(new NodeDto());

        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();

        Mockito.when(nodeRepository.findByDeletedFalse())
                .thenReturn(nodes);

        Mockito.when(modelMapper.map(nodes, listType))
                .thenReturn(dtos);

        List<NodeDto> response = nodeService.findAll();

        Assertions.assertEquals(
                1,
                response.size()
        );
    }

    @Test
    void findAllWithPaginationShouldReturnNodeDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Node node = new Node();
        node.setIdentifier("N1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Page<Node> page =
                new PageImpl<>(List.of(node));

        Mockito.when(nodeRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

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
                "N1",
                response.getContent().get(0).getIdentifier()
        );

        Mockito.verify(nodeRepository)
                .findByDeletedFalse(pageable);
    }

    @Test
    void findAllWithSearchShouldReturnNodeDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Node node = new Node();
        node.setIdentifier("N1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Page<Node> page =
                new PageImpl<>(List.of(node));

        Mockito.when(
                nodeRepository.findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                        "N1",
                        pageable
                )
        ).thenReturn(page);

        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        Page<NodeDto> response =
                nodeService.findAll(pageable, "N1");

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );

        Assertions.assertEquals(
                "N1",
                response.getContent().get(0).getIdentifier()
        );

        Mockito.verify(nodeRepository)
                .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                        "N1",
                        pageable
                );
    }
}