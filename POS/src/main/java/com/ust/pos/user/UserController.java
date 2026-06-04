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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {

    public static final Authentication AUTHENTICATION = SecurityContextHolder.getContext().getAuthentication();
    public static final String COLOUR = "colour";
    public static final String MESSAGE = "message";
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
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("user", response);
        return "user/user";
    }


    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute UserDto userDto) {
        UserDto response = userService.update(userDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute(COLOUR, "red");
        } else {
            model.addAttribute(MESSAGE,
                    "User '" + userDto.getName() + "' updated successfully");
            model.addAttribute(COLOUR, "green");
        }
        UserDto updatedUser = userService.findByUserName(userDto.getUsername());
        model.addAttribute("user", updatedUser);
        model.addAttribute("roles", roleService.findAll());
        return "user/user";
    }


    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String username, RedirectAttributes redirectAttributes) {
        String loggedInUsername = AUTHENTICATION.getName();
        if (loggedInUsername.equalsIgnoreCase(username)) {
            redirectAttributes.addFlashAttribute(MESSAGE,
                    "Sorry! You cannot delete your own account.");
            redirectAttributes.addFlashAttribute(COLOUR, "red");
            return "redirect:/user/list";
        }
        userService.delete(username);
        redirectAttributes.addFlashAttribute(MESSAGE,
                "User '" + username + "' deleted successfully");
        redirectAttributes.addFlashAttribute(COLOUR, "green");
        return "redirect:/user/list";
    }
}
