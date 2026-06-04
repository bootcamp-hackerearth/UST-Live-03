package com.ust.pos.role;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/role")
public class RoleController {


    public static final String REDIRECT_ROLE_LIST = "redirect:/role/list";
    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute("roles", roleService.findAll());
        return "role/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute RoleDto roleDto) {
        return "role/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute RoleDto roleDto) {
        RoleDto response = roleService.save(roleDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "role/add";
        }
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        RoleDto response = roleService.findByIdentifier(identifier);
        model.addAttribute("role", response);
        return "role/role";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute RoleDto roleDto) {
        RoleDto response = roleService.update(roleDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        roleService.delete(identifier);
        return REDIRECT_ROLE_LIST;
    }
}
