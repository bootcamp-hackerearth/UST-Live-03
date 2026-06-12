package com.ust.pos.api.node;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/node")

public class NodeControllerApi extends BaseController {

    @Autowired
    private NodeService nodeService;

    @Autowired
    private RoleService roleService;

    @PostMapping("/list")
    public PaginatedResponseDto<NodeDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
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
    public NodeDto updatePost(Model model, @RequestBody NodeDto nodeDto) {
        return nodeService.update(nodeDto);
    }

    @PostMapping("/delete")
    public Boolean delete(@RequestBody NodeDto nodeDto) {
        try {
            nodeService.delete(nodeDto.getIdentifier());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/getnodes")
    public List<NodeDto> getNodesForRoles() {
        return nodeService.getNodesForRoles();
    }


    @PostMapping("/toggle")
    public boolean changeStatus(@RequestBody NodeDto nodeDto) {
        try {
            nodeService.changeStatus(nodeDto.getIdentifier(), nodeDto.getStatus());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}