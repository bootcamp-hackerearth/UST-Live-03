package com.ust.pos.api.node;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/node")
public class NodeApiController extends BaseController {

    @Autowired
    private NodeService nodeService;
    @Autowired
    private RoleService roleService;

    @PostMapping("/list")
    public PaginationResponseDto<NodeDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
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

    @PostMapping("/toggle")
    public NodeDto toggleStatus(@RequestBody NodeDto dto) {
        return nodeService.updateStatus(dto.getIdentifier(), dto.isStatus());
    }

    @GetMapping("/get")
    public NodeDto update(@RequestParam String identifier) {
        return nodeService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public NodeDto updatePost(@RequestBody NodeDto nodeDto) {
        return nodeService.update(nodeDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            nodeService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
