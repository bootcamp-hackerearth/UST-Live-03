package com.ust.pos.node;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/node")

public class NodeController {
    public static final String REDIRECT_NODE_LIST = "redirect:/node/list";

    public static final String ROLES = "roles";
    @Autowired
    private NodeService nodeService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public String home(Model node, Pageable pageable) {
        node.addAttribute("nodes", nodeService.findAll(pageable));
        return "node/list";
    }

    @GetMapping("/add")
    public String add(Model node, @ModelAttribute NodeDto userDto) {
        node.addAttribute("userDto", userDto);
        node.addAttribute(ROLES, roleService.findActiveStatus());
        return "node/add";
    }

    @PostMapping("/add")
    public String addPost(Model node, @ModelAttribute NodeDto userDto) {
        NodeDto response = nodeService.save(userDto);
        node.addAttribute("userDto", response);
        if (!response.isSuccess()) {
            node.addAttribute("message", response.getMessage());
            return "node/add";
        }
        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/get")
    public String update(Model node, @RequestParam String identifier,Pageable pageable) {
        NodeDto response = nodeService.findByIdentifier(identifier);
        node.addAttribute("node", response);
        node.addAttribute(ROLES, roleService.findAll(pageable));
        return "node/node";
    }

    @PostMapping("/update")
    public String updatePost(Model node, @ModelAttribute NodeDto userDto,Pageable pageable) {
        NodeDto response = nodeService.update(userDto);
        node.addAttribute(ROLES, roleService.findAll(pageable));
        if (!response.isSuccess()) {
            node.addAttribute("message", response.getMessage());
        }
        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model node, @RequestParam String identifier) {
        nodeService.delete(identifier);
        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status) {
        nodeService.changeToggleStatus(identifier, status);
        return REDIRECT_NODE_LIST;
    }
}
