package com.ust.pos.user;

import com.ust.pos.dto.UserDto;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import org.jspecify.annotations.Nullable;
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
        model.addAttribute("userDto", response);
        model.addAttribute("roles", roleService.findAll());
        return "user/user";
    }

    @PostMapping("/update")
    public String updateUser(Model model, @ModelAttribute("userDto") UserDto userDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String loggedInUser = authentication.getName();
            UserDto response = userService.update(userDto);
            if (!response.isSuccess()) {
                model.addAttribute("roles", roleService.findAll());
                model.addAttribute("message", response.getMessage());
                return "user/user";
            }
            if (loggedInUser.equals(userDto.getOldUsername())) {
                SecurityContextHolder.clearContext();
                return "redirect:/login";
            }
        }
        return "redirect:/user/list";
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String username) {
        @Nullable Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            String loggedInUser = authentication.getName();
            userService.delete(username);
            if (loggedInUser.equals(username)) {
                SecurityContextHolder.clearContext();
                return "redirect:/login";
            }
        }
        return "redirect:/user/list";
    }
}