package com.ust.pos.api.node;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.node.service.NodeService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/node")
public class NodeApiController extends BaseController {
    private final NodeService nodeService;

    public NodeApiController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public WsDto<NodeDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage()
                , paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Node> example = buildGlobalSearchSpec(Node.class, paginationDto.getKeyword());
            if (example != null) {
                return nodeService.findAll(example, pageable);
            }
        }
        return nodeService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public NodeDto add(@RequestBody NodeDto nodeDto) {
        return nodeService.save(nodeDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('ADMIN')")
    public NodeDto getByIdentifier(@RequestParam String identifier) {
        return nodeService.findByIdentifier(identifier);
    }

    @GetMapping("/getNodesForRoles")
    public List<NodeDto> getNodesForRoles() {
        return nodeService.getNodesForRoles();
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public NodeDto update(@RequestBody NodeDto nodeDto) {
        return nodeService.update(nodeDto);
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAuthority('ADMIN')")
    public NodeDto toggleStatus(@RequestParam String identifier) {
        return nodeService.toggleStatus(identifier);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public boolean delete(@RequestParam String identifier) {
        try {
            nodeService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
