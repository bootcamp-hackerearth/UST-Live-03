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

    public static final String MESSAGE = "message";
    public static final String MESSAGE1 = "message";
    @Autowired
    private NodeService nodeService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("nodes", nodeService.findAll(pageable));
        return "node/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute NodeDto nodeDto, Pageable pageable) {
        model.addAttribute("roles", roleService.findAll(pageable));
        return "node/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute NodeDto userDto) {
        NodeDto response = nodeService.save(userDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        }
        return "redirect:/node/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier, Pageable pageable) {
        NodeDto response = nodeService.findByIdentifier(identifier);
        model.addAttribute("node", response);
        model.addAttribute("roles", roleService.findAll(pageable));
        return "node/node";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute NodeDto nodeDto) {
        NodeDto response = nodeService.update(nodeDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        }
        return "redirect:/node/list";
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier, Pageable pageable) {
        nodeService.delete(identifier);
        model.addAttribute("nodes", nodeService.findAll(pageable));
        model.addAttribute(MESSAGE1, "Node deleted successfully");
        return "node/list";
    }
}
