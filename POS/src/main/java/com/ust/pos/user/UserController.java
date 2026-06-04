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
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("users", userService.findAll(pageable));
        return "user/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String username, Pageable pageable) {
        UserDto response = userService.findByUserName(username);
        model.addAttribute("user", response);
        model.addAttribute("roles", roleService.findAll(pageable));
        return "user/user";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute UserDto userDto) {
        UserDto response = userService.update(userDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "user/user";
        }
        return "redirect:/user/list";
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String username, Pageable pageable) {

        boolean deletingSelf = userService.getCurrentUser(username);

        userService.delete(username);

        if (deletingSelf) {
            return "redirect:/logout";
        }

        model.addAttribute("users", userService.findAll(pageable));
        model.addAttribute("message", "User deleted successfully");

        return "user/list";
    }

}
