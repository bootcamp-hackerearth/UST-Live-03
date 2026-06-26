package com.ust.pos;

import com.ust.pos.node.service.NodeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private NodeService nodeService;

    public HomeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("node", nodeService.getNodesForRoles());
        return "home";
    }
}