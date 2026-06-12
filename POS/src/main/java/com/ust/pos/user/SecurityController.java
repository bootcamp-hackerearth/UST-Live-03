package com.ust.pos.user;

import com.ust.pos.dto.UserDto;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SecurityController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/register")
    public String add(Model model, @ModelAttribute UserDto userDto, Pageable pageable) {
        model.addAttribute("userDto", userDto);
        model.addAttribute("roles", roleService.findAll(pageable));
        return "register";
    }

    @PostMapping("/register")
    public String addPost(Model model, @ModelAttribute UserDto userDto, Pageable pageable) {
        UserDto response = userService.save(userDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("roles", roleService.findAll(pageable));
            return "register";
        }

        return "redirect:/";
    }
}
