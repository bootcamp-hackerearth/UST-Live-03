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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/node")
public class NodeRestController extends BaseController {

    private final NodeService nodeService;

    public NodeRestController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @PostMapping("/list")
    public WsDto<NodeDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Node> example = buildGlobalSearchSpec(Node.class, paginationDto.getKeyword());
            if (example != null) {
                return nodeService.findAll(example, pageable);
            }
        }

        return nodeService.findAll(pageable);
    }

    @PostMapping("/add")
    public NodeDto addPost(@RequestBody NodeDto nodeDto) {
        return nodeService.save(nodeDto);
    }

    @GetMapping("/get")
    public NodeDto update(@RequestParam String identifier) {
        return nodeService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public NodeDto updatePost(@RequestBody NodeDto nodeDto) {
        return nodeService.update(nodeDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            nodeService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/getNodesForRoles")
    public List<NodeDto> getNodesForRoles(NodeDto nodeDto) {
        return nodeService.getNodesForRoles();
    }

    @PutMapping("/toggle-status")
    public boolean toggleStatus(@RequestParam String identifier) {
        try {
            nodeService.toggleStatus(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
