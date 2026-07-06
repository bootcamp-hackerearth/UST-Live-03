package com.ust.pos.api.node;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.Node;
import com.ust.pos.node.service.NodeService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public PaginationResponseDto<NodeDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Node> example = buildGlobalSearchSpec(Node.class, paginationDto.getKeyword());
            if (example != null) {
                return nodeService.findAll(example, pageable);
            }
        }

        return nodeService.findAll(pageable);
    }

    @GetMapping("/listnodeforroles")
    public List<NodeDto> listNodeForRoles() {
        return nodeService.getNodesForRoles();
    }

    @PostMapping("/add")
    public NodeDto add(@RequestBody NodeDto nodeDto) {
        return nodeService.save(nodeDto);
    }

    @PutMapping("/toggle")
    public NodeDto toggleStatus(@RequestBody NodeDto dto) {
        return nodeService.updateStatus(dto.getIdentifier(), dto.isStatus());
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
}
