package com.ust.pos.user;

import com.ust.pos.dto.UserDto;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute("users", userService.findAll(null));
        return "user/list";
    }

    @GetMapping("/get")
    public String update(@ModelAttribute UserDto userDto, Model model, @RequestParam String username) {
        UserDto response = userService.findByUserName(username);
        model.addAttribute("userDto", response);
        model.addAttribute("roles", roleService.findAll(null));
        return "user/user";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute UserDto userDto) {
        UserDto response = userService.update(userDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
        }
        return "redirect:/user/list";
    }

    @GetMapping("/delete")
    public String delete(@ModelAttribute UserDto userDto, Model model, @RequestParam String username) {
        userService.delete(username);
        return "redirect:/user/list";
    }
}
