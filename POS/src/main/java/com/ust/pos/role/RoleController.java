package com.ust.pos.role;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/role")
public class RoleController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/role/list";
    public static final String NODES = "nodes";
    public static final String ROLE_ADD = "role/add";
    @Autowired
    private RoleService roleService;

    @Autowired
    private NodeService nodeService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("roles", roleService.findAll(pageable));
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return "role/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute RoleDto userDto) {
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return ROLE_ADD;
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute RoleDto userDto) {
        RoleDto response = roleService.save(userDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return ROLE_ADD;
        }
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        RoleDto response = roleService.findByIdentifier(identifier);
        model.addAttribute("role", response);
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return "role/role";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute RoleDto userDto) {
        RoleDto response = roleService.update(userDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "role/role";
        }
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        roleService.delete(identifier);
        return REDIRECT_ROLE_LIST;
    }
}
