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

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String username, @ModelAttribute UserDto userDto) {
        UserDto response = userService.findByUserName(username);
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("user", response);
        return "user/user";
    }

    @PostMapping("/update")
    public String updatePost(Model model,
                             @ModelAttribute UserDto userDto,
                             RedirectAttributes redirectAttributes) {

        UserDto response = userService.update(userDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "user/user";
        }

        redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        return "redirect:/user/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String username,
                         RedirectAttributes redirectAttributes) {

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

        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        return "redirect:/user/list";
    }
}