package com.ust.pos.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
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
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public String home(Model model, @ModelAttribute PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),paginationDto.getSortField());
        model.addAttribute("users", userService.findAll(pageable));
        return "user/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String username, @ModelAttribute UserDto userDto) {
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
            return "user/user";
        }
        return "redirect:/user/list";
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String loggedInUser = authentication.getName();
            userService.delete(identifier);
            if (loggedInUser.equals(identifier)) {
                SecurityContextHolder.clearContext();
                return "redirect:/login";
            }
        }
        return "redirect:/user/list";
    }

    @PostMapping("/save")
    @ResponseBody
    public UserDto save(@RequestBody UserDto userDto) {

        return userService.save(userDto);
    }
}