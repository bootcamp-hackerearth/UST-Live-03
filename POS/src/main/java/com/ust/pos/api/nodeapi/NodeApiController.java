package com.ust.pos.api.nodeapi;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
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
    RoleService roleService;

    @Autowired
    private NodeService nodeService;

    @PostMapping("/list")
    public WsDto<NodeDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
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

    @PostMapping("/toggle-status")
    public NodeDto toggle(@RequestParam String identifier) {
        return nodeService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<NodeDto> findByStatus() {
        return nodeService.findIfTrue();
    }

    @GetMapping("/getNodesForRoles")
    public List<NodeDto> getNodesForRoles(){
        return nodeService.getNodesForRoles();
    }
}