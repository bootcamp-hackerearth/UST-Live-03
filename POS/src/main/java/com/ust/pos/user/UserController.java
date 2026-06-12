package com.ust.pos.user;

import com.ust.pos.dto.UserDto;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("users", userService.findAll(pageable));
        return "user/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String username, Pageable pageable) {
        UserDto response = userService.findByUserName(username);
        model.addAttribute("userDto", response);
        model.addAttribute("rolesList", roleService.findAll(pageable));
        return "user/user";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute UserDto userDto, Pageable pageable) {
        UserDto response = userService.update(userDto);
        if (!response.isSuccess()) {
            model.addAttribute("userDto", response);
            model.addAttribute("rolesList", roleService.findAll(pageable));
            model.addAttribute("success", false);
            model.addAttribute("message", response.getMessage());
            return "user/user";
        }
        return "redirect:/user/list";
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String username) {
        userService.delete(username);
        return "redirect:/user/list";
    }
}