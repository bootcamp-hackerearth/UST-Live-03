package com.ust.pos.api.node;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.node.service.NodeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/node")
public class NodeApiController extends BaseController {
    private final NodeService nodeService;

    public NodeApiController(
            NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public WsDto<NodeDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortField()
        );

        Page<NodeDto> pageResult =
                nodeService.findAll(
                        pageable,
                        paginationDto.getSearch()
                );

        WsDto<NodeDto> output = new WsDto<>();
        output.setContent(pageResult.getContent());
        output.setPage(pageResult.getNumber());
        output.setSizePerPage(pageResult.getSize());
        output.setTotalPages(pageResult.getTotalPages());

        return output;
    }

    @GetMapping("/list")
    public List<NodeDto> list() {
        return nodeService.findAll();
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public NodeDto addPost(@RequestBody NodeDto nodeDto) {
        return nodeService.save(nodeDto);
    }

    @GetMapping("/get")
    public NodeDto update(@RequestParam String identifier) {
        return nodeService.findByIdentifier(identifier);
    }

    @GetMapping("/roles")
    public List<NodeDto> getNodesForRoles() {
        return nodeService.getNodesForRoles();
    }

    @PutMapping("/update")
    public NodeDto updatePost(@RequestBody NodeDto nodeDto) {
        return nodeService.update(nodeDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public boolean delete(@RequestParam String identifier) {
        try {
            nodeService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}