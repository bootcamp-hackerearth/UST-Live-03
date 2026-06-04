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

    public static final String REDIRECT_ROLE_LIST1 = "redirect:/role/list";
    public static final String REDIRECT_ROLE_LIST = REDIRECT_ROLE_LIST1;
    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
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
            model.addAttribute("message", response.getMessage());
            model.addAttribute("success", false);
            return "role/add";
        }
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        RoleDto response = roleService.findByIdentifier(identifier);
        model.addAttribute("role", response);
        model.addAttribute("roleDto", response);
        return "role/role";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute RoleDto roleDto) {
        RoleDto response = roleService.update(roleDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("role", response);
            model.addAttribute("roleDto", response);
            return "role/role";
        }
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        roleService.delete(identifier);
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status) {
        roleService.changeRoleStatus(identifier, status);
        return REDIRECT_ROLE_LIST1;
    }
}