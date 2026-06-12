package com.ust.pos.api.node;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.node.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/node")
public class NodeControllerApi extends BaseController {

    public static final String REDIRECT_NODE_LIST = "redirect:/node/list";
    public static final String ROLES_LIST = "rolesList";

    @Autowired
    private NodeService nodeService;

    @PostMapping("/list")
    public WsDto<NodeDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return nodeService.findAll(pageable);

    }

    @GetMapping("/nodeForRole")
    public List<NodeDto> getNodesForRoles() {
        return nodeService.getNodesForRoles();
    }

    @PostMapping("/add")
    public NodeDto addPost(@RequestBody NodeDto nodeDto) {
        return nodeService.save(nodeDto);
    }

    @GetMapping("/update")
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

    @PostMapping("/changeStatus")
    public NodeDto changestatus(@RequestBody NodeDto nodeDto) {
        return nodeService.changeNodeStatus(nodeDto.getIdentifier(), nodeDto.isStatus());
    }

    @GetMapping("/findAllActive")
    public List<NodeDto> allactive() {
        return nodeService.findActiveNode();
    }
}