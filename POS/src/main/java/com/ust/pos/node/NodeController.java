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

    public static final String ROLES = "roles";
    public static final String REDIRECT_NODE_LIST = "redirect:/node/list";
    @Autowired
    private NodeService nodeService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("nodes", nodeService.findAll());
        return "node/list";
    }

    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("nodeDto", new NodeDto());
        model.addAttribute(ROLES, roleService.findAll());
        return "node/add";
    }

    @PostMapping("/add")
    public String addPost(Model model,@ModelAttribute NodeDto nodeDto) {
        NodeDto response = nodeService.save(nodeDto);
        if (!response.isSuccess()) {
            model.addAttribute(ROLES, roleService.findAll());
            model.addAttribute("message", response.getMessage());
            return "node/add";
        }
        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model,@RequestParam String identifier) {
        NodeDto nodeDto = nodeService.findByIdentifier(identifier);
        model.addAttribute("nodeDto", nodeDto);
        model.addAttribute(ROLES, roleService.findAll());
        return "node/node";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute NodeDto nodeDto) {
        NodeDto response = nodeService.update(nodeDto);
        if (!response.isSuccess()) {
            model.addAttribute(ROLES, roleService.findAll());
            model.addAttribute("message", response.getMessage());
            return "node/node";
        }
        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        nodeService.delete(identifier);
        return REDIRECT_NODE_LIST;
    }
}