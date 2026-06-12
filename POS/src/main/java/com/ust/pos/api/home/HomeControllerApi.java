package com.ust.pos.api.home;

import com.ust.pos.node.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeControllerApi {

    @Autowired
    private NodeService nodeService;

    @GetMapping
    public Object home() {
         return nodeService.getNodesForRoles();
    }
}