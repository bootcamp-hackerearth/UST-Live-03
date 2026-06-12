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
    private UserService userService;
    @Autowired
    private NodeService nodeService;

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        model.addAttribute("nodes", nodeService.getNodesForRoles());
        UserDto user = userService.findByIdentifier(principal.getName());
        model.addAttribute("user", user);
        return "home";
    }

}
