package com.ust.pos.node;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/node")
public class NodeController {
    public static final String REDIRECT_NODE_LIST = "redirect:/node/list";
    public static final String ROLES_LIST = "rolesList";
    @Autowired
    RoleService roleService;

    @Autowired
    private NodeService nodeService;

    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute("nodes", nodeService.findAll());
        return "node/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute NodeDto nodeDto) {
        model.addAttribute(ROLES_LIST, roleService.findAll());
        return "node/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute NodeDto nodeDto) {
        NodeDto response = nodeService.save(nodeDto);
        model.addAttribute(ROLES_LIST, roleService.findAll());
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("success", false);
            return "node/add";
        }
        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        NodeDto response = nodeService.findByIdentifier(identifier);
        model.addAttribute(ROLES_LIST, roleService.findAll());
        model.addAttribute("node", response);
        model.addAttribute("nodeDto", response);
        return "node/node";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute NodeDto nodeDto) {
        NodeDto response = nodeService.update(nodeDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("node", response);
            model.addAttribute("nodeDto", response);
            model.addAttribute(ROLES_LIST, roleService.findAll());
        }
        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        nodeService.delete(identifier);
        return REDIRECT_NODE_LIST;
    }
}