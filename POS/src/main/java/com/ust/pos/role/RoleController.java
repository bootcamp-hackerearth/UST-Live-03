package com.ust.pos.role;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/role")
public class RoleController {

    public static final String REDIRECT_LIST = "redirect:/role/list";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String ERROR_MESSAGE = "errorMessage";

    @Autowired
    private RoleService roleService;


    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("roles", roleService.findAll(pageable));
        return "role/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("role", new RoleDto());
        return "role/add";
    }


    @PostMapping("/add")
    public String addPost(@ModelAttribute RoleDto roleDto, RedirectAttributes redirectAttributes) {

        RoleDto response = roleService.save(roleDto);
        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, response.getMessage());
            return "/role/add";
        }
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Role added successfully");
        return REDIRECT_LIST;
    }


    @GetMapping("/get")
    public String get(@RequestParam String identifier, Model model) {
        model.addAttribute("role", roleService.findByIdentifier(identifier));
        return "role/role";
    }


    @PostMapping("/update")
    public String update(@ModelAttribute RoleDto roleDto, RedirectAttributes redirectAttributes) {

        RoleDto response = roleService.update(roleDto);
        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, response.getMessage());
        } else {
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Role updated successfully");
        }
        return REDIRECT_LIST;
    }


    @PostMapping("/delete")
    public String delete(@RequestParam String identifier, RedirectAttributes redirectAttributes) {
        roleService.delete(identifier);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Role deleted successfully");
        return REDIRECT_LIST;
    }

    @PostMapping("/toggle")
    public String toggle(@RequestParam String identifier, RedirectAttributes redirectAttributes) {
        roleService.toggleStatus(identifier);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Role status updated successfully");
        return REDIRECT_LIST;
    }
}