package com.ust.pos.user;

import com.ust.pos.dto.UserDto;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SecurityController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        Pageable pageable = PageRequest.of(0, 20);
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("roles", roleService.findAll(pageable));
        return "register";
    }

    @PostMapping("/register")
    public String registerPost(@ModelAttribute("userDto") UserDto userDto, Model model, RedirectAttributes redirectAttributes) {

        UserDto response = userService.save(userDto);

        if (!response.isSuccess()) {
            model.addAttribute("roles", roleService.findAll(PageRequest.of(0, 20)));
            model.addAttribute("message", response.getMessage());
            return "register";
        }

        redirectAttributes.addFlashAttribute("successMessage", "User registered successfully! Please login.");
        return "redirect:/login";
    }
}