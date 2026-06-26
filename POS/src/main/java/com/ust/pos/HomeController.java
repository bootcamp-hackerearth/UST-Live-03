package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final NodeService nodeService;

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        model.addAttribute("nodes", nodeService.getNodesForRoles());
        UserDto user = userService.findByIdentifier(principal.getName());
        model.addAttribute("user", user);
        return "home";
    }

}
