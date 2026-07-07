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
import org.springframework.data.jpa.domain.Specification;
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

    // GET NODES FOR ROLES

    @Test
    void getNodesForRolesTest() {

        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        "admin", "pwd", new ArrayList<>());

        Mockito.when(authentication.getPrincipal()).thenReturn(springUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setUsername("admin");
        user.setRoles(List.of("ADMIN"));

        Mockito.when(userRepository.findByUsername("admin")).thenReturn(user);

        Node node = new Node();
        node.setIdentifier("NODE1");
        node.setRoles(List.of("ADMIN"));

        Mockito.when(nodeRepository.findByIsDeleteFalse())
                .thenReturn(List.of(node));

        Mockito.when(nodeRepository.findByIdentifierAndIsDeleteFalse("NODE1"))
                .thenReturn(node);

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("NODE1", result.get(0).getIdentifier());

        SecurityContextHolder.clearContext();
    }

    @Test
    void getNodesForRoles_WhenNoAuth_ShouldReturnEmpty() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());

        SecurityContextHolder.clearContext();
    }

    // SAVE

    @Test
    void saveTest_Success() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node entity = new Node();

        Mockito.when(nodeRepository.findByIdentifierAndIsDeleteFalse("N1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Node.class)).thenReturn(entity);

        Mockito.when(nodeRepository.save(entity)).thenReturn(entity);

        NodeDto response = nodeService.save(dto);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(nodeRepository).save(entity);
    }

    @Test
    void saveTest_WhenExists_ShouldFail() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifierAndIsDeleteFalse("N1"))
                .thenReturn(new Node());

        NodeDto response = nodeService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(nodeRepository, Mockito.never()).save(Mockito.any());
    }

    // UPDATE

    @Test
    void updateTest_Success() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node existing = new Node();
        existing.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifierAndIsDeleteFalse("N1"))
                .thenReturn(existing);

        Mockito.doNothing().when(modelMapper).map(dto, existing);

        Mockito.when(nodeRepository.save(existing)).thenReturn(existing);

        NodeDto response = nodeService.update(dto);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(nodeRepository).save(existing);
    }

    @Test
    void updateTest_WhenNotFound_ShouldFail() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifierAndIsDeleteFalse("N1"))
                .thenReturn(null);

        NodeDto response = nodeService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(nodeRepository, Mockito.never()).save(Mockito.any());
    }

    // DELETE (SOFT DELETE)

    @Test
    void deleteTest_ShouldSoftDelete() {

        Node node = new Node();
        node.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifierAndIsDeleteFalse("N1"))
                .thenReturn(node);

        Mockito.when(nodeRepository.save(node)).thenReturn(node);

        nodeService.delete("N1");

        Assertions.assertTrue(node.isDelete());
        Mockito.verify(nodeRepository).save(node);
    }

    @Test
    void deleteTest_WhenNotFound_ShouldDoNothing() {

        Mockito.when(nodeRepository.findByIdentifierAndIsDeleteFalse("N1"))
                .thenReturn(null);

        nodeService.delete("N1");

        Mockito.verify(nodeRepository, Mockito.never()).save(Mockito.any());
    }

    // FIND ALL

    @Test
    void findAllTest() {

        List<Node> entities = List.of(new Node());

        List<NodeDto> dtos = List.of(new NodeDto());

        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();

        Mockito.when(nodeRepository.findByIsDeleteFalse()).thenReturn(entities);
        Mockito.when(modelMapper.map(entities, listType)).thenReturn(dtos);

        List<NodeDto> result = nodeService.findAll();

        Assertions.assertEquals(1, result.size());
    }

    // FIND BY IDENTIFIER

    @Test
    void findByIdentifierTest() {

        Node node = new Node();
        node.setIdentifier("N1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifierAndIsDeleteFalse("N1"))
                .thenReturn(node);

        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        NodeDto result = nodeService.findByIdentifier("N1");

        Assertions.assertEquals("N1", result.getIdentifier());
    }

    // PAGINATION

    @Test
    void findAll_WithPagination_NoSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Node node = new Node();
        node.setIdentifier("NODE1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Page<Node> page = new PageImpl<>(List.of(node));

        Mockito.when(nodeRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        Page<NodeDto> result = nodeService.findAll(pageable, null);

        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals("NODE1", result.getContent().get(0).getIdentifier());
    }

    @Test
    void findAllPageableWithSearchTest() {
        Pageable pageable =
                PageRequest.of(0, 10);
        Node node = new Node();
        Page<Node> page =
                new PageImpl<>(List.of(node));
        Mockito.when(nodeRepository.findAll(
                        Mockito.<Specification<Node>>any(),
                        Mockito.eq(pageable)))
                .thenReturn(page);
        Page<NodeDto> result =
                nodeService.findAll(pageable, "Admin");
        Assertions.assertEquals(
                1,
                result.getContent().size()
        );
        Mockito.verify(nodeRepository)
                .findAll(
                        Mockito.<Specification<Node>>any(),
                        Mockito.eq(pageable)
                );
    }

    @Test
    void findAll_WithBlankSearch_ShouldFallback() {

        Pageable pageable = PageRequest.of(0, 10);

        Node node = new Node();
        node.setIdentifier("NODE1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Page<Node> page = new PageImpl<>(List.of(node));

        Mockito.when(nodeRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        Page<NodeDto> result = nodeService.findAll(pageable, " ");

        Assertions.assertEquals(1, result.getContent().size());
    }
}