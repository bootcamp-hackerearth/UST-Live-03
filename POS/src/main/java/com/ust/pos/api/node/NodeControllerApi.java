package com.ust.pos.api.node;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.model.Node;
import com.ust.pos.node.service.NodeService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/node")
public class NodeControllerApi extends BaseController {

    private final NodeService nodeService;

    public NodeControllerApi(NodeService nodeService){
        this.nodeService=nodeService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin')")
    public PageDto<NodeDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Node> spec = buildGlobalSearchSpec(Node.class, paginationDto.getKeyword());
            return nodeService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return nodeService.findAll(pageable);
    }


    @GetMapping("/identifier")
    public NodeDto getNodeByIdentifier(@RequestParam String identifier) {
        return nodeService.findByIdentifier(identifier);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin')")
    public NodeDto addPost(@RequestBody NodeDto nodeDto) {
        return nodeService.save(nodeDto);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Admin')")
    public NodeDto updatePost(@RequestBody NodeDto nodeDto) {
        return nodeService.update(nodeDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Admin')")
    public boolean delete(@RequestParam String identifier) {
        try {
            nodeService.delete(identifier);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    @GetMapping("/nodeforroles")
    public List<NodeDto> getNodesForRoles() {
        return nodeService.getNodesForRoles();
    }

    @PostMapping("/toggleStatus")
    @PreAuthorize("hasAuthority('Admin')")
    public void toggleStatus(@RequestParam String identifier) {
        nodeService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<NodeDto> findByStatus() {
        return nodeService.findActiveNodes();
    }
}

