package com.ust.pos.node;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/node")
public class NodeController {

    public static final String REDIRECT_NODE_LIST = "redirect:/node/list";
    public static final String ROLES = "roles";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String ERROR_MESSAGE = "errorMessage";

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
    public String add(Model model) {
        model.addAttribute("nodeDto", new NodeDto());
        model.addAttribute(ROLES, roleService.findIfTrue());
        return "node/add";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute NodeDto nodeDto,
                          RedirectAttributes redirectAttributes) {

        NodeDto response = nodeService.save(nodeDto);

        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, response.getMessage());
            return "redirect:/node/add";
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Node added successfully!");
        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model,
                         @RequestParam String identifier,
                         RedirectAttributes redirectAttributes) {
        try {
            NodeDto response = nodeService.findByIdentifier(identifier);
            model.addAttribute("node", response);
            model.addAttribute(ROLES, roleService.findIfTrue());
            return "node/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Node not found!");
            return REDIRECT_NODE_LIST;
        }
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute NodeDto nodeDto,
                             RedirectAttributes redirectAttributes) {

        NodeDto response = nodeService.update(nodeDto);

        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, response.getMessage());
            return "redirect:/node/get?identifier=" + nodeDto.getIdentifier();
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Node updated successfully!");
        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier,
                         RedirectAttributes redirectAttributes) {

        try {
            nodeService.delete(identifier);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Node deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Failed to delete node!");
        }

        return REDIRECT_NODE_LIST;
    }

    @PostMapping("/toggle")
    public String toggleNode(@RequestParam String identifier,
                             RedirectAttributes redirectAttributes) {

        try {
            nodeService.toggleStatus(identifier);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Status updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Failed to update status!");
        }

        return REDIRECT_NODE_LIST;
    }
}
