package com.ust.pos;

import com.ust.pos.node.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private NodeService nodeService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("nodes", nodeService.getNodesForRoles());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        String username = authentication.getName();
        model.addAttribute("name", username);
        return "home";
    }
}
