package com.ust.pos.node;

import com.ust.pos.api.BaseController;
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
public class NodeController extends BaseController {

    public static final String REDIRECT_NODE_LIST = "redirect:/node/list";
    @Autowired
    private NodeService nodeService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("node", new NodeDto());
        model.addAttribute("roles", roleService.findAllActive());
        return "node/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute NodeDto nodeDto) {
        NodeDto response = nodeService.save(nodeDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("nodes", nodeService.findAll(pageable));
        return "node/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        NodeDto response = nodeService.findByIdentifier(identifier);
        model.addAttribute("roles", roleService.findAllActive());
        model.addAttribute("node", response);
        return "node/node";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute NodeDto nodeDto) {
        NodeDto response = nodeService.update(nodeDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        nodeService.delete(identifier);
        return REDIRECT_NODE_LIST;
    }
}
