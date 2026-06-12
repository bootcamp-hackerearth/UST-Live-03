package com.ust.pos.api.node;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.node.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/node")
public class NodeControllerApi extends BaseController {

    @Autowired
    private NodeService nodeService;

    @PostMapping("/list")
    public PageDto  <NodeDto> node(@RequestBody PaginationDto paginationDto) {
        Pageable pageable=getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),paginationDto.getSortDirection(),paginationDto.getSortField());
        return nodeService.findAll(pageable);
    }

    @GetMapping("/identifier")
    public NodeDto getNodeByIdentifier(@RequestParam String identifier) {
        return nodeService.findByIdentifier(identifier);
    }

    @PostMapping("/add")
    public NodeDto addPost(@RequestBody NodeDto nodeDto) {
        return nodeService.save(nodeDto);
    }

    @PostMapping("/update")
    public NodeDto updatePost(@RequestBody NodeDto nodeDto) {
        return nodeService.update(nodeDto);
    }

    @GetMapping("/delete")
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

    @GetMapping("/toggleStatus")
    public void toggleStatus(@RequestParam String identifier) {
        nodeService.toggleStatus(identifier);
    }
    @GetMapping("/findByStatus")
    public List<NodeDto> findByStatus() {
        return nodeService.findActiveNodes();
    }
}

