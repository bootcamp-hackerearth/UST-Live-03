package com.ust.pos.user;

import com.ust.pos.dto.UserDto;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
        model.addAttribute("roles",roleService.findAll());
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
    public String delete(@RequestParam String username,
                         HttpServletRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        String loggedInUsername = auth.getName();

        userService.delete(username);

        if (loggedInUsername.equals(username)) {

            SecurityContextHolder.clearContext();

            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            return "redirect:/login";
        }

        return "redirect:/user/list";
    }
}
