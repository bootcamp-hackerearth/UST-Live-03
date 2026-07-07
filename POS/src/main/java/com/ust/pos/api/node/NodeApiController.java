package com.ust.pos.api.node;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.node.service.NodeService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/node")
public class NodeApiController extends BaseController {

    private final NodeService nodeService;


    public NodeApiController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @PostMapping("/list")
    public WsDto<NodeDto> home(@RequestBody PaginationDto paginationDto) throws Exception
    {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        Example<Node> example = buildSearchProbe(Node.class, paginationDto.getSearch());
        Page<NodeDto> pageResult = nodeService.findAll(example, pageable);

        WsDto<NodeDto> response = new WsDto<>();

        response.setDtoList(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPage(pageResult.getTotalPages());
        response.setTotalRecords(pageResult.getTotalElements());

        return response;
    }

    @PostMapping("/add")
    public NodeDto addPost(@RequestBody NodeDto userDto) {
        return nodeService.save(userDto);
    }

    @GetMapping("/get")
    public NodeDto update(@RequestParam String identifier) {
        return nodeService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public NodeDto updatePost(@RequestBody NodeDto userDto) {
        return nodeService.update(userDto);
    }

    @DeleteMapping("/delete")
    public Boolean delete(@RequestParam String identifier) {
        try
        {
            nodeService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
