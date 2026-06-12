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
public class NodeApiController extends BaseController {

    @Autowired
    private NodeService nodeService;

    @PostMapping("/list")
    public WsDto<NodeDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        return nodeService.findAll(pageable);
    }

    @PostMapping("/add")
    public NodeDto addPost(@RequestBody NodeDto userDto) {

        return nodeService.save(userDto);
    }

    @PostMapping("/get")
    public NodeDto update(@RequestBody String identifier) {

        return nodeService.findByIdentifier(identifier);
    }

    @PostMapping("/getnodesforroles")
    public List<NodeDto> getNodesForRoles() {

        return nodeService.getNodesForRoles();
    }

    @PostMapping("/update")
    public NodeDto updatePost(@RequestBody NodeDto nodeDto) {

        return nodeService.update(nodeDto);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody String identifier) {

        try {
            nodeService.delete(identifier);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
