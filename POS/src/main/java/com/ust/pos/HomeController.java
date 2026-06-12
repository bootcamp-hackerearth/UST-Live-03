package com.ust.pos;

import com.ust.pos.node.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private NodeService nodeService;

    @GetMapping("/")
    public String home(Model model) {

        try {
            model.addAttribute("nodes", nodeService.getNodesForRoles());
            return "home";
        } catch (RuntimeException ex) {
            if ("USER_DELETED".equals(ex.getMessage())) {
                return "redirect:/login";
            }

            return "redirect:/login";
        }
    }
}