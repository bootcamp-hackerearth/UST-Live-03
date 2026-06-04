package com.ust.pos.api.node;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("nodeApiController")
@RequestMapping("/api/nodes")
public class NodeController extends BaseController {

    @Autowired
    private NodeService nodeService;

    @Autowired
    private RoleService roleService;

    @PostMapping("/list")
    public ResponseEntity<List<NodeDto>> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        List<NodeDto> nodes = nodeService.findAll(pageable);
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<NodeDto> getByIdentifier(@PathVariable String identifier) {
        NodeDto response = nodeService.findByIdentifier(identifier);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<NodeDto> save(@RequestBody NodeDto nodeDto) {
        NodeDto response = nodeService.save(nodeDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{identifier}")
    public ResponseEntity<NodeDto> update(@PathVariable String identifier, @RequestBody NodeDto nodeDto) {
        nodeDto.setIdentifier(identifier);
        NodeDto response = nodeService.update(nodeDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete/{identifier}")
    public ResponseEntity<Boolean> delete(@PathVariable String identifier) {
        try {
            nodeService.delete(identifier);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }
}