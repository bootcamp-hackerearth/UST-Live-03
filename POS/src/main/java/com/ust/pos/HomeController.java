package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private NodeService nodeService;

    @Autowired
    private UserService userService;

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