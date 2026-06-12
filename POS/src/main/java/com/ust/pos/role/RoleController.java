package com.ust.pos.role;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/role")
public class RoleController {

    public static final String MESSAGE = "message";
    public static final String MESSAGE1 = "message";
    public static final String MESSAGE2 = "message";

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("roles", roleService.findAll(pageable));
        return "role/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute RoleDto userDto) {
        return "role/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute RoleDto userDto) {
        RoleDto response = roleService.save(userDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        }
        return "redirect:/role/list";
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
            model.addAttribute(MESSAGE1, response.getMessage());
        }
        return "redirect:/role/list";
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier, Pageable pageable) {
        roleService.delete(identifier);
        model.addAttribute("roles", roleService.findAll(pageable));
        model.addAttribute(MESSAGE2, "Role deleted successfully");
        return "role/list";
    }
}
