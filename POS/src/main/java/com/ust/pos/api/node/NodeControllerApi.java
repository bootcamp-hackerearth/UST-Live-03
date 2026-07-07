package com.ust.pos.api.node;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.model.Node;
import com.ust.pos.node.service.NodeService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/node")
@RequiredArgsConstructor
public class NodeControllerApi extends BaseController {

    private final NodeService nodeService;

    @PostMapping("/list")
    public PaginatedResponseDto<NodeDto> list(@RequestBody PaginationDto paginationDto) {
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
    public NodeDto get(@RequestParam String identifier) {
        return nodeService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public NodeDto updatePost(@RequestBody NodeDto nodeDto) {
        return nodeService.update(nodeDto);
    }

    @DeleteMapping("/delete")
    public NodeDto delete(@RequestBody NodeDto nodeDto) {
        try {
            return nodeService.delete(nodeDto.getIdentifier());
        } catch (Exception e) {
            NodeDto errorDto = new NodeDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Delete failed: " + e.getMessage());
            return errorDto;
        }
    }

    @GetMapping("/getnodes")
    public List<NodeDto> getNodesForRoles() {
        return nodeService.getNodesForRoles();
    }

    @PatchMapping("/toggle")
    public boolean changeStatus(@RequestBody NodeDto nodeDto) {
        try {
            nodeService.changeStatus(nodeDto.getIdentifier(), nodeDto.getStatus());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}