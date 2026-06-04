package com.ust.pos.node;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.node.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/node")

public class NodeController {

    public static final String NODE_DTO = "nodeDto";
    public static final String ROLES = "roles";
    public static final String REDIRECT_NODE_LIST = "redirect:/node/list";

    @Autowired
    private NodeService nodeService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public String home(Model model) {

        model.addAttribute("nodes", nodeService.findAll());
        return "node/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute NodeDto nodeDto) {

        model.addAttribute(ROLES, roleService.findAll());
        model.addAttribute(NODE_DTO, nodeDto);
        return "node/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute NodeDto nodeDto) {

        NodeDto response = nodeService.save(nodeDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(NODE_DTO, nodeDto);
            model.addAttribute(ROLES, roleService.findAll());
            return "node/add";
        }
        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {

        NodeDto response = nodeService.findByIdentifier(identifier);
        model.addAttribute("node", response);
        model.addAttribute(ROLES, roleService.findAll());
        return "node/node";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute NodeDto nodeDto) {

        NodeDto response = nodeService.update(nodeDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(ROLES, roleService.findAll());
            model.addAttribute(NODE_DTO, nodeDto);
            return "node/node";
        }
        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {

        nodeService.delete(identifier);
        return REDIRECT_NODE_LIST;
    }
}
