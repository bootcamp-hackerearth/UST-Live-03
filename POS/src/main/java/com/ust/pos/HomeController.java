package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    private final NodeService nodeService;

    private final UserService userService;

    public HomeController(NodeService nodeService, UserService userService) {
        this.nodeService = nodeService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        model.addAttribute("nodes", nodeService.getNodesForRoles());

        if (principal != null) {
            UserDto loggedInUser = userService.findByUserName(principal.getName());
            model.addAttribute("loggedInUser", loggedInUser);
        }
        return "home";
    }
}