package com.ust.pos;

import com.ust.pos.node.service.NodeService;
import com.ust.pos.user.service.UserService;
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

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("name", username);
        }

        model.addAttribute("nodes", nodeService.getNodesForRoles());

        return "home";
    }
}
