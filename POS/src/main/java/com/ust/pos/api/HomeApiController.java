package com.ust.pos.api;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.node.service.NodeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HomeApiController {
    private final NodeService nodeService;

    public HomeApiController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @PostMapping("/")
    public List<NodeDto> home() {
        return nodeService.getNodesForRoles();
    }
}
