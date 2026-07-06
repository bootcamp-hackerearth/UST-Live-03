package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

    @Test
    void testFindByIdentifier_Success() {
        String identifier = "NODE-001";

        Node node = new Node();
        node.setIdentifier(identifier);

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier(identifier);

        when(nodeRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(node);
        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(nodeDto);

        NodeDto result = nodeService.findByIdentifier(identifier);

        assertEquals(identifier, result.getIdentifier());
    }

    @Test
    void testSave_Success() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE-001");

        Node node = new Node();
        node.setIdentifier("NODE-001");

        when(nodeRepository.findByIdentifier("NODE-001"))
                .thenReturn(null);
        when(modelMapper.map(nodeDto, Node.class))
                .thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        assertTrue(result.isSuccess());
        assertEquals("Node created successfully", result.getMessage());

        verify(nodeRepository).save(node);
    }

    @Test
    void testSave_AlreadyExists() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE-001");

        Node existingNode = new Node();
        existingNode.setIdentifier("NODE-001");
        existingNode.setDeleted(false);

        when(nodeRepository.findByIdentifier("NODE-001"))
                .thenReturn(existingNode);

        NodeDto result = nodeService.save(nodeDto);

        assertFalse(result.isSuccess());
        assertEquals("Node with identifier - NODE-001 already exists", result.getMessage());

        verify(nodeRepository, never()).save(any(Node.class));
    }

    @Test
    void testSave_DeletedNodeExists() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE-001");

        Node existingNode = new Node();
        existingNode.setIdentifier("NODE-001");
        existingNode.setDeleted(true);

        when(nodeRepository.findByIdentifier("NODE-001"))
                .thenReturn(existingNode);

        NodeDto result = nodeService.save(nodeDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Node with identifier -NODE-001 was deleted and cannot be created again.",
                result.getMessage()
        );

        verify(nodeRepository, never()).save(any(Node.class));
    }

    @Test
    void testUpdate_Success() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE-001");

        Node existingNode = new Node();
        existingNode.setIdentifier("NODE-001");

        when(nodeRepository.findByIdentifierAndDeletedFalse("NODE-001"))
                .thenReturn(existingNode);

        NodeDto result = nodeService.update(nodeDto);

        assertTrue(result.isSuccess());
        assertEquals("Node updated successfully", result.getMessage());

        verify(modelMapper).map(nodeDto, existingNode);
        verify(nodeRepository).save(existingNode);
    }

    @Test
    void testUpdate_NotFound() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE-001");

        when(nodeRepository.findByIdentifierAndDeletedFalse("NODE-001"))
                .thenReturn(null);

        NodeDto result = nodeService.update(nodeDto);

        assertFalse(result.isSuccess());
        assertEquals("Node with identifier - NODE-001 not found", result.getMessage());

        verify(nodeRepository, never()).save(any(Node.class));
    }

    @Test
    void testDelete_Success() {
        String identifier = "NODE-001";

        Node node = new Node();
        node.setIdentifier(identifier);
        node.setDeleted(false);

        when(nodeRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(node);

        nodeService.delete(identifier);

        assertTrue(node.getDeleted());

        verify(nodeRepository).save(node);
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Node node = new Node();
        node.setIdentifier("NODE-001");

        List<Node> nodeList = List.of(node);
        Page<Node> nodePage = new PageImpl<>(nodeList, pageable, nodeList.size());

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE-001");

        List<NodeDto> dtoList = List.of(nodeDto);

        when(nodeRepository.findAllByDeletedFalse(pageable))
                .thenReturn(nodePage);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void testGetNodesForRoles_Success() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "admin",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User currentUser = new User();
        currentUser.setIdentifier("admin");
        currentUser.setRoles(List.of("ROLE_ADMIN"));

        Node node = new Node();
        node.setIdentifier("NODE-001");
        node.setRoles(List.of("ROLE_ADMIN"));

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE-001");

        when(userRepository.findByIdentifierAndDeletedFalse("admin"))
                .thenReturn(currentUser);

        when(nodeRepository.findAllByDeletedFalse())
                .thenReturn(List.of(node));

        when(nodeRepository.findByIdentifierAndDeletedFalse("NODE-001"))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(nodeDto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertEquals(1, result.size());
        assertEquals("NODE-001", result.get(0).getIdentifier());

        SecurityContextHolder.clearContext();
    }

    @Test
    void findAll_ShouldReturnWsDtoWithNodeDtos() {
        Specification<Node> specification = (root, query, criteriaBuilder) -> null;
        Pageable pageable = PageRequest.of(0, 10);

        Node node = new Node();
        node.setId(1L);
        node.setIdentifier("NODE001");
        node.setPath("Dashboard");

        List<Node> nodeList = List.of(node);
        Page<Node> nodePage =
                new PageImpl<>(nodeList, pageable, nodeList.size());

        NodeDto nodeDto = new NodeDto();
        nodeDto.setId(1L);
        nodeDto.setIdentifier("NODE001");
        nodeDto.setPath("Dashboard");

        List<NodeDto> nodeDtoList = List.of(nodeDto);

        when(nodeRepository.findAll(specification, pageable))
                .thenReturn(nodePage);

        when(modelMapper.map(eq(nodeList), any(Type.class)))
                .thenReturn(nodeDtoList);

        WsDto<NodeDto> result = nodeService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(nodeDtoList, result.getDtoList());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(nodeRepository, times(1)).findAll(specification, pageable);
        verify(modelMapper, times(1)).map(eq(nodeList), any(Type.class));
    }

}