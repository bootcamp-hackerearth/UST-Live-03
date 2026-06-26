package com.ust.pos;

import com.ust.pos.node.service.NodeService;
import com.ust.pos.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final NodeService nodeService;
    private final UserService userService;

    public HomeController(NodeService nodeService, UserService userService) {
        this.nodeService = nodeService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String loggedInUser = authentication.getName();
            String currentUser = userService.findByUserName(loggedInUser).getName();
            model.addAttribute("nodes", nodeService.getNodesForRoles());
            model.addAttribute("userName", currentUser);
            return "home";
        }
        return null;
    }
}
