package com.ust.pos.api.node;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.node.service.NodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("nodeApiController")
@RequestMapping("/api/nodes")
public class NodeController extends BaseController {
    private final NodeService nodeService;

    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin')")
    public WsDto<NodeDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Node> spec = buildGlobalSearchSpec(Node.class, paginationDto.getKeyword());
            return nodeService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return nodeService.findAll(pageable);
    }

    @GetMapping("/getNodesForRoles")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<List<NodeDto>> myNodes() {
        return ResponseEntity.ok(nodeService.getNodesForRoles());
    }

    @GetMapping("/{identifier}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<NodeDto> getByIdentifier(@PathVariable String identifier) {
        NodeDto response = nodeService.findByIdentifier(identifier);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<NodeDto> save(@RequestBody NodeDto nodeDto) {
        NodeDto response = nodeService.save(nodeDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{identifier}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<NodeDto> update(@PathVariable String identifier, @RequestBody NodeDto nodeDto) {
        nodeDto.setIdentifier(identifier);
        NodeDto response = nodeService.update(nodeDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{identifier}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Boolean> delete(@PathVariable String identifier) {
        try {
            nodeService.delete(identifier);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }
}