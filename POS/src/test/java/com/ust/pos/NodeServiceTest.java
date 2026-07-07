package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Node;
import com.ust.pos.models.NodeRepository;
import com.ust.pos.models.User;
import com.ust.pos.models.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    void saveTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Node mapped = new Node();
        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        when(modelMapper.map(dto, Node.class)).thenReturn(mapped);
        NodeDto result = nodeService.save(dto);
        assertNotNull(result);
        verify(nodeRepository).save(mapped);
        NodeDto dto2 = new NodeDto();
        dto2.setIdentifier("N2");
        Node mapped2 = new Node();
        mapped2.setStatus(null);
        when(nodeRepository.findByIdentifier("N2")).thenReturn(null);
        when(modelMapper.map(dto2, Node.class)).thenReturn(mapped2);
        nodeService.save(dto2);
        assertTrue(mapped2.getStatus());
        Node active = new Node();
        active.setDeleted(false);
        when(nodeRepository.findByIdentifier("N3")).thenReturn(active);
        result = nodeService.save(new NodeDto() {{setIdentifier("N3");}});
        assertFalse(result.isSuccess());
        Node deleted = new Node();
        deleted.setDeleted(true);
        when(nodeRepository.findByIdentifier("N4")).thenReturn(deleted);
        result = nodeService.save(new NodeDto() {{setIdentifier("N4");}});
        assertFalse(result.isSuccess());
    }

    @Test
    void findByIdentifierUpdateAndDeleteTest() {
        Node existing = new Node();
        existing.setIdentifier("N1");
        existing.setCreatedBy("admin");
        existing.setCreatedOn(LocalDateTime.now());
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        when(nodeRepository.findByIdentifierAndDeletedFalse("N1")).thenReturn(existing);
        when(modelMapper.map(existing, NodeDto.class)).thenReturn(dto);
        NodeDto result = nodeService.findByIdentifier("N1");
        assertNotNull(result);
        result = nodeService.update(dto);
        assertNotNull(result);
        verify(modelMapper).map(dto, existing);
        verify(nodeRepository).save(existing);
        nodeService.delete("N1");
        assertTrue(existing.getDeleted());
        when(nodeRepository.findByIdentifierAndDeletedFalse("N2")).thenReturn(null);
        NodeDto response = nodeService.update(new NodeDto() {{setIdentifier("N2");}});
        assertFalse(response.isSuccess());
        assertNull(nodeService.findByIdentifier("N2"));
        nodeService.delete("N2");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Node> nodes = List.of(new Node());
        Page<Node> page = new PageImpl<>(nodes, pageable, 1);
        when(nodeRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(nodes), any(Type.class))).thenReturn(List.of(new NodeDto()));
        WsDto<NodeDto> result = nodeService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked")
        Specification<Node> specification = mock(Specification.class);
        Page<Node> page = new PageImpl<>(List.of(new Node()), pageable, 1);
        when(nodeRepository.findAll(specification, pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(new NodeDto()));
        WsDto<NodeDto> result = nodeService.findAll(specification, pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        verify(nodeRepository).findAll(specification, pageable);
    }

    @Test
    void getNodesForRolesTest() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        org.springframework.security.core.userdetails.User springUser = new org.springframework.security.core.userdetails.User("user1", "pass", List.of());
        when(authentication.getPrincipal()).thenReturn(springUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User user = new User();
        user.setUsername("user1");
        user.setRoles(List.of("ADMIN"));
        when(userRepository.findByUsername("user1")).thenReturn(user);
        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(List.of("ADMIN"));
        when(nodeRepository.findAllByDeletedFalse()).thenReturn(List.of(node));
        when(nodeRepository.findByIdentifierAndDeletedFalse("N1")).thenReturn(node);
        when(modelMapper.map(any(Node.class), eq(NodeDto.class))).thenReturn(new NodeDto());
        List<NodeDto> result = nodeService.getNodesForRoles();
        assertEquals(1, result.size());
        node.setRoles(List.of("USER"));
        result = nodeService.getNodesForRoles();
        assertTrue(result.isEmpty());
        SecurityContextHolder.clearContext();
        result = nodeService.getNodesForRoles();
        assertTrue(result.isEmpty());
    }
}