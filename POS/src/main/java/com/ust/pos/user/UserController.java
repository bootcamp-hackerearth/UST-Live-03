package com.ust.pos.user;

import com.ust.pos.dto.UserDto;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller

@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public String home(Model model) {

        model.addAttribute("users", userService.findAll());
        return "user/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String username) {

        UserDto response = userService.findByUserName(username);
        model.addAttribute("user", response);
        model.addAttribute("roles", roleService.findAll());
        return "user/user";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute UserDto userDto) {

        UserDto response = userService.update(userDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("user", userDto);
            model.addAttribute("roles", roleService.findAll());
            return "user/user";
        }
        return "redirect:/user/list";
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String username) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            String loggedInUser = authentication.getName();

            if (loggedInUser != null) {
                userService.delete(username);

                if (loggedInUser.equals(username)) {
                    SecurityContextHolder.clearContext();
                    return "redirect:/login";
                }
            }
        }
        return "redirect:/user/list";
    }
}
