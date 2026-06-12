package com.ust.pos.api;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.node.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HomeApiController {
    @Autowired
    private NodeService nodeService;

    @PostMapping("/")
    public List<NodeDto> home() {
        return nodeService.getNodesForRoles();
    }
}
