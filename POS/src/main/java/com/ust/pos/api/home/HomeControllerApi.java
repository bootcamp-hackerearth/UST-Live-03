package com.ust.pos.api.home;

import com.ust.pos.node.service.NodeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeControllerApi {

    private final NodeService nodeService;

    public HomeControllerApi(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping
    public Object home() {
         return nodeService.getNodesForRoles();
    }
}