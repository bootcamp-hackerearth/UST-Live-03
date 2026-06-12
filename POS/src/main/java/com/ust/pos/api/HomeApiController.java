package com.ust.pos.api;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HomeApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private NodeService nodeService;

    @GetMapping("/api/home")
    public List<NodeDto> home() {
        return nodeService.getNodesForRoles();
    }
}
