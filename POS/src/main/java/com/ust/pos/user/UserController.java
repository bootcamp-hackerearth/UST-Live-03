package com.ust.pos.user;

import com.ust.pos.dto.UserDto;
import com.ust.pos.model.UserRepository;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import jakarta.transaction.Transactional;
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
    public static final String USER_USER1 = "user/user";
    public static final String USER_USER = "user/user";
    public static final String REDIRECT_USER_LIST = "redirect:/user/list";

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("users", userService.findAll(pageable));
        return "user/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String username, Pageable pageable) {
        UserDto response = userService.findByUserName(username);
        model.addAttribute("roles", roleService.findAll(pageable));
        model.addAttribute("userDto", response);
        return USER_USER;
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute UserDto userDto, Pageable pageable) {
        UserDto response = userService.update(userDto);
        model.addAttribute("userDto", response);
        model.addAttribute("roles", roleService.findAll(pageable));
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return USER_USER1;
        }
        return REDIRECT_USER_LIST;
    }

    @Transactional
    @GetMapping("/delete")
    public String delete(@RequestParam String username) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String loggedInUsername = authentication.getName();
            if (username.equals(loggedInUsername)) {
                return "redirect:/user/list?error=loggedInUser";
            }
            userRepository.deleteByUsername(username);
            return REDIRECT_USER_LIST;
        }
        return null;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam Long id, boolean status) {
        userService.changeToggleStatus(id, status);
        return REDIRECT_USER_LIST;
    }
}

