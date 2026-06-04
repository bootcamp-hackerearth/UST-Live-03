package com.ust.pos.user;

import com.ust.pos.dto.UserDto;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecurityController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {

        if (error != null) {
            model.addAttribute("errorMsg", "Invalid email or password");
        }

        return "login";
    }

    @GetMapping("/register")
    public String add(Model model, @ModelAttribute UserDto userDto) {
        model.addAttribute("roles", roleService.findAll());
        return "register";
    }

    @PostMapping("/register")
    public String addPost(Model model, @ModelAttribute UserDto userDto) {

        UserDto response = userService.save(userDto);

        if (!response.isSuccess()) {
            model.addAttribute("errorMsg", response.getMessage());
            model.addAttribute("roles", roleService.findAll());
            return "register";
        }

        return "login";
    }
}
