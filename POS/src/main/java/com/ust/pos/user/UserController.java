package com.ust.pos.user;

import com.ust.pos.dto.UserDto;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private final RoleService roleService;

    private final UserService userService;

    public UserController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("users", userService.findAll(pageable));
        return "user/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String username, @ModelAttribute UserDto user, Pageable pageable) {
        UserDto response = userService.findByUserName(username);
        model.addAttribute("roles", roleService.findAll(pageable));
        model.addAttribute("user", response);
        return "user/user";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute UserDto userDto) {
        UserDto response = userService.update(userDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("user", response);
            return "user/user";
        }
        return "redirect:/user/list";
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String loggedInUser = authentication.getName();
            userService.delete(username);
            if (loggedInUser.equals(username)) {
                SecurityContextHolder.clearContext();
                return "redirect:/logout";
            }
            return "redirect:/user/list";
        }
        return null;
    }
}
