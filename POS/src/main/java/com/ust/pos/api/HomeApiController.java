package com.ust.pos.api;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.node.service.NodeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
public class HomeApiController {

    private final NodeService nodeService;

    public HomeApiController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping
    public List<NodeDto> home() {
        return nodeService.getNodesForRoles();
    }

}