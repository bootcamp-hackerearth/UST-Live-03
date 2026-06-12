package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.times;

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

    @Test
    void saveTestSuccess() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        Node node = new Node();
        Mockito.when(modelMapper.map(nodeDto, Node.class))
                .thenReturn(node);

        Mockito.when(nodeRepository.save(node))
                .thenReturn(node);

        NodeDto response = nodeService.save(nodeDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        Node node = new Node();
        node.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(node);

        NodeDto response = nodeService.save(nodeDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Node node = new Node();
        node.setIdentifier("Admin");

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(node);

        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(nodeDto);

        NodeDto response = nodeService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");
        nodeDto.setSuccess(true);

        Node existingNode = new Node();
        existingNode.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(existingNode);

        Mockito.when(nodeRepository.save(existingNode))
                .thenReturn(existingNode);

        NodeDto response = nodeService.update(nodeDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        NodeDto response = nodeService.update(nodeDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(nodeRepository)
                .deleteByIdentifier("Admin");

        boolean result = nodeService.delete("Admin");

        Assertions.assertTrue(result);

        Mockito.verify(nodeRepository, times(1))
                .deleteByIdentifier("Admin");
    }

    @Test
    void toggleStatusTest() {
        Node node = new Node();
        node.setIdentifier("Admin");
        node.setStatus(true);

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(node);

        Mockito.when(nodeRepository.save(node))
                .thenReturn(node);

        nodeService.toggleStatus("Admin");

        Assertions.assertFalse(node.getStatus());

        Mockito.verify(nodeRepository)
                .save(node);
    }

    @Test
    void toggleStatusTestFailure() {
        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        nodeService.toggleStatus("Admin");

        Mockito.verify(nodeRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findActiveNodesTest() {
        Node node = new Node();
        node.setIdentifier("Admin");
        node.setStatus(true);

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        List<Node> nodeList = List.of(node);

        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();

        Mockito.when(nodeRepository.findByStatusTrue())
                .thenReturn(nodeList);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(nodeList),
                        Mockito.eq(listType)
                )
        ).thenReturn(List.of(nodeDto));

        List<NodeDto> response = nodeService.findActiveNodes();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(
                "Admin",
                response.get(0).getIdentifier()
        );
    }

    @Test
    void findAllPaginationTest() {

        Node node = new Node();
        node.setIdentifier("Admin");

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Node> nodePage =
                new PageImpl<>(List.of(node), pageable, 1);

        Mockito.when(nodeRepository.findAll(pageable))
                .thenReturn(nodePage);

        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(nodePage.getContent()),
                        Mockito.eq(listType)
                )
        ).thenReturn(List.of(nodeDto));

        PageDto<NodeDto> response =
                nodeService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(
                "Admin",
                response.getDtoList().get(0).getIdentifier()
        );
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void getNodesForRolesTest() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "admin@test.com",
                        "password",
                        List.of()
                );

        Authentication authentication =
                Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal())
                .thenReturn(principal);

        SecurityContext securityContext =
                Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setRoles(List.of("ADMIN"));

        Mockito.when(userRepository.findByUsername("admin@test.com"))
                .thenReturn(user);

        Node node = new Node();
        node.setIdentifier("dashboard");
        node.setRoles(List.of("ADMIN"));

        Mockito.when(nodeRepository.findAll())
                .thenReturn(List.of(node));

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("dashboard");

        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(nodeDto);

        List<NodeDto> response = nodeService.getNodesForRoles();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(
                "dashboard",
                response.get(0).getIdentifier()
        );
    }

    @Test
    void getNodesForRolesTest_NoAuth() {
        SecurityContext securityContext =
                Mockito.mock(SecurityContext.class);

        Mockito.when(securityContext.getAuthentication())
                .thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        List<NodeDto> response =
                nodeService.getNodesForRoles();

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRolesTest_InvalidPrincipal() {
        Authentication authentication =
                Mockito.mock(Authentication.class);

        Mockito.when(authentication.getPrincipal())
                .thenReturn("anonymousUser");

        SecurityContext securityContext =
                Mockito.mock(SecurityContext.class);

        Mockito.when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        List<NodeDto> response =
                nodeService.getNodesForRoles();

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isEmpty());
    }
}