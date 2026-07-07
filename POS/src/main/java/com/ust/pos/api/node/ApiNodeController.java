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

public class ApiNodeController extends BaseController {

    private final NodeService nodeService;

    public ApiNodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @PostMapping("/list")
    public WsDto<NodeDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable= getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Node> example = buildGlobalSearchSpec(Node.class, paginationDto.getKeyword());
            if (example != null) {
                return nodeService.findAll(example, pageable);
            }
        }
        return nodeService.findAll(pageable);
    }


    @PostMapping("/add")
    public NodeDto addPost(@RequestBody NodeDto userDto) {
        return nodeService.save(userDto);
    }

    @GetMapping("/get")
    public NodeDto update(@RequestParam String identifier) {
        return nodeService.findByIdentifier(identifier);
    }

    @GetMapping("/getNodesForRoles")
    public List<NodeDto> getNodesForRoles(){
        return nodeService.getNodesForRoles();
    }

    @PutMapping("/update")
    public NodeDto updatePost(@RequestBody NodeDto userDto) {
        return nodeService.update(userDto);
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

    @PostMapping("/toggle")
    public NodeDto toggle(@RequestBody NodeDto nodeDto) {
        return nodeService.changeToggleStatus(nodeDto.getIdentifier(), nodeDto.isStatus());
    }

    @PostMapping("/findActiveStatus")
    public List<NodeDto> findActive() {
        return nodeService.findActiveStatus();
    }

}
