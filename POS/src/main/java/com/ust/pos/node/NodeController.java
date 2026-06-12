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
    public static final String NODE_NODE = "node/node";

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
    public String add(Model model, Pageable pageable, @ModelAttribute NodeDto nodeDto) {

        model.addAttribute(ROLES, roleService.findAll(pageable));
        return "node/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, Pageable pageable, @ModelAttribute NodeDto nodeDto) {

        NodeDto response = nodeService.save(nodeDto);

        if (!response.isSuccess()) {
            model.addAttribute(ROLES, roleService.findAll(pageable));
            model.addAttribute("message", response.getMessage());
            return NODE_NODE;
        }

        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, Pageable pageable, @RequestParam String identifier) {

        NodeDto nodeDto = nodeService.findByIdentifier(identifier);

        if (nodeDto == null) {
            return REDIRECT_NODE_LIST;
        }

        model.addAttribute("nodeDto", nodeDto);
        model.addAttribute(ROLES, roleService.findAll(pageable));

        return NODE_NODE;
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute NodeDto nodeDto) {

        NodeDto response = nodeService.update(nodeDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return NODE_NODE;
        }

        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {

        nodeService.delete(identifier);
        return REDIRECT_NODE_LIST;
    }
}
