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

    public static final String REDIRECT_ROLE_LIST = "redirect:/role/list";
    public static final String ROLES = "roles";
    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(ROLES, roleService.findAll(pageable));
        return "role/list";
    }

    @GetMapping("/add")
    public String add(Model model, Pageable pageable, @ModelAttribute RoleDto userDto) {
        model.addAttribute("nodes", roleService.findAll(pageable));
        model.addAttribute(ROLES, roleService.findAll(pageable));
        return "role/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, Pageable pageable, @ModelAttribute RoleDto userDto) {
        RoleDto response = roleService.save(userDto);

        if (!response.isSuccess()) {
            model.addAttribute("error", response.getMessage());
            model.addAttribute(ROLES, roleService.findAll(pageable));
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
    public String updatePost(Model model, @ModelAttribute RoleDto userDto) {
        RoleDto response = roleService.update(userDto);
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

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        roleService.toggleStatus(identifier);
        return REDIRECT_ROLE_LIST;
    }
}
