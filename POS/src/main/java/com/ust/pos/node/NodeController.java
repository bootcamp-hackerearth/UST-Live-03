package com.ust.pos.node;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/node")
public class NodeController {
    private static final String NODE_LIST = "node/list";
    private static final String NODE_ADD = "node/add";
    private static final String NODE_VIEW = "node/node";
    private static final String REDIRECT_NODE_LIST = "redirect:/node/list";

    @Autowired
    private NodeService nodeService;
    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("nodes", nodeService.getNodesForRoles());
        return NODE_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute NodeDto nodeDto) {
        model.addAttribute("roles", roleService.findByStatusTrue());
        return NODE_ADD;
    }

    @PostMapping("/save")
    public String add(RedirectAttributes redirectAttributes, @ModelAttribute NodeDto nodeDto) {
        NodeDto response = nodeService.save(nodeDto);
        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute("message", "Node with identifier already exists");
        }
        return REDIRECT_NODE_LIST;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public NodeDto toggleStatus(@RequestBody NodeDto dto) {
        return nodeService.updateStatus(dto.getIdentifier(), dto.isStatus());
    }

    @GetMapping("/get")
    public String update(@RequestParam String identifier, Model model, @ModelAttribute NodeDto nodeDto) {
        NodeDto response = nodeService.findByIdentifier(identifier);
        model.addAttribute("roles", roleService.findAll(null));
        model.addAttribute("nodeDto", response);
        return NODE_VIEW;
    }

    @PostMapping("/update")
    public String updatePost(RedirectAttributes redirectAttributes, @ModelAttribute NodeDto nodeDto) {
        NodeDto response = nodeService.update(nodeDto);
        redirectAttributes.addFlashAttribute("message", response.getMessage());
        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        nodeService.delete(identifier);
        return REDIRECT_NODE_LIST;
    }
}
