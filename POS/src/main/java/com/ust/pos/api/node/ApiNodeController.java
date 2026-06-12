package com.ust.pos.api.node;

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

public class ApiNodeController extends BaseController {

    @Autowired
    private NodeService nodeService;

    @Autowired
    private RoleService roleService;

    @PostMapping("/list")
    public WsDto<NodeDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable= getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
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

    @PostMapping("/update")
    public NodeDto updatePost(@RequestBody NodeDto userDto) {
        return nodeService.update(userDto);

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

    @PostMapping("/toggle")
    public NodeDto toggle(@RequestBody NodeDto nodeDto) {
        return nodeService.changeToggleStatus(nodeDto.getIdentifier(), nodeDto.isStatus());
    }

    @PostMapping("/findActiveStatus")
    public List<NodeDto> findActive() {
        return nodeService.findActiveStatus();
    }
}
