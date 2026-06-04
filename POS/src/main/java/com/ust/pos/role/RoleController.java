package com.ust.pos.role;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/role")
public class RoleController {

    private static final String ROLE_LIST = "role/list";
    private static final String ROLE_ADD = "role/add";
    private static final String ROLE_VIEW = "role/role";
    private static final String REDIRECT_ROLE_LIST = "redirect:/role/list";

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("roles", roleService.findAll());
        return ROLE_LIST;
    }

    @GetMapping("/add")
    public String add(@ModelAttribute RoleDto roleDto) {
        return ROLE_ADD;
    }

    @PostMapping("/add")
    public String addPost(RedirectAttributes redirectAttributes, @ModelAttribute RoleDto roleDto) {
        RoleDto response = roleService.save(roleDto);
        redirectAttributes.addFlashAttribute("message", response.getMessage());
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/get")
    public String update(@ModelAttribute RoleDto roleDto, Model model, @RequestParam String identifier) {
        RoleDto response = roleService.findByIdentifier(identifier);
        model.addAttribute("roleDto", response);
        return ROLE_VIEW;
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
    public String delete(@RequestParam String identifier) {
        roleService.delete(identifier);
        return REDIRECT_ROLE_LIST;
    }
}
