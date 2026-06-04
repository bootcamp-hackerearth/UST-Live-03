package com.ust.pos.user;

import com.ust.pos.dto.UserDto;
import com.ust.pos.node.service.NodeService;
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
    public static final String REDIRECT_USER_LIST = "redirect:/user/list";
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private NodeService nodeService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("nodes", nodeService.getNodesForRoles());
        model.addAttribute("users", userService.findAll(pageable));
        return "user/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String username, Pageable pageable) {
        UserDto response = userService.findByUserName(username);
        model.addAttribute("user", response);
        model.addAttribute("roles", roleService.findAll(pageable));
        model.addAttribute("nodes", nodeService.getNodesForRoles());
        return "user/user";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute UserDto userDto, Pageable pageable) {
        UserDto response = userService.update(userDto);
        if (!response.isSuccess()) {
            model.addAttribute("user", response);
            model.addAttribute("roles", roleService.findAll(pageable));
            model.addAttribute(MESSAGE, response.getMessage());
            return "user/user";
        }
        return REDIRECT_USER_LIST;
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
            return REDIRECT_USER_LIST;
        }
        return null;
    }
}
