package com.ust.pos.api;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.node.service.NodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeApiController {

    private final NodeService nodeService;

    @GetMapping("/api/home")
    public List<NodeDto> home() {
        return nodeService.getNodesForRoles();
    }

}
