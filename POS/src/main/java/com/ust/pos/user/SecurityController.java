package com.ust.pos.user;

import com.ust.pos.dto.UserDto;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SecurityController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/login")
    public String login(Model model, @ModelAttribute UserDto userDto) {
        return "login";
    }

    @GetMapping("/register")
    public String add(Model model, @ModelAttribute UserDto userDto) {
        model.addAttribute("roles", roleService.findAll());
        return "register";
    }

    @PostMapping("/register")
    public String addPost(Model model, @ModelAttribute UserDto userDto, RedirectAttributes redirectAttributes) {
        UserDto response = userService.save(userDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("roles", roleService.findAll());
            return "register";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isLoggedIn = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        if (isLoggedIn) {
            return "redirect:/user/list";
        } else {
            redirectAttributes.addFlashAttribute("successToast",
                    "Account registered successfully. Please login.");
            return "redirect:/login";
        }
    }
}
