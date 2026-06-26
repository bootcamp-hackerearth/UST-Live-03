package com.ust.pos;

import com.ust.pos.node.service.NodeService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final NodeService nodeService;

    public HomeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping("/")
    public String home(Model model, Pageable pageable) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("name", username);
        }

        model.addAttribute("nodes", nodeService.getNodesForRoles(pageable));

        return "home";
    }
}
