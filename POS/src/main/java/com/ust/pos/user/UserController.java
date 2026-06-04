package com.ust.pos.user;

import com.ust.pos.dto.UserDto;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    public static final String MESSAGE = "message";
    public static final String ROLES = "roles";
    public static final String USER_USER = "user/user";
    @Autowired
    public RoleService roleService;
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("users", userService.findAll(pageable));
        return "user/list";
    }


    @GetMapping("/get")
    public String update(Model model, Pageable pageable, @RequestParam String username) {
        UserDto response = userService.findByUserName(username);
        model.addAttribute("user", response);
        model.addAttribute(ROLES, roleService.findAll(pageable));
        return USER_USER;
    }


    @PostMapping("/update")
    public String updatePost(Model model, Pageable pageable, @ModelAttribute UserDto userDto, @RequestParam String oldUsername) {
        UserDto existingUser = userService.findByUserName(oldUsername);

        if (existingUser == null) {
            model.addAttribute(MESSAGE, "User not found.");
            model.addAttribute(ROLES, roleService.findAll(pageable));
            return USER_USER;
        }

        if (!oldUsername.equalsIgnoreCase(userDto.getUsername())) {

            UserDto emailCheck = userService.findByUserName(userDto.getUsername());

            if (emailCheck != null) {
                model.addAttribute(MESSAGE,
                        " Email already exists. Please use a new email.");
                model.addAttribute("user", userDto);
                model.addAttribute(ROLES, roleService.findAll(pageable));
                return USER_USER;
            }
        }
        UserDto response = userService.update(oldUsername, userDto);

        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute("user", userDto);
            model.addAttribute(ROLES, roleService.findAll(pageable));
            return USER_USER;
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
