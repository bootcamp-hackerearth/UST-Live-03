package com.ust.pos.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public String home(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("users", userService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "user/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String username) {
        UserDto response = userService.findByUserName(username);
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("user", response);
        model.addAttribute("roles", roleService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "user/user";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute UserDto userDto) {
        UserDto response = userService.update(userDto);
        PaginationDto paginationDto = new PaginationDto();
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("user", userDto);
            model.addAttribute("roles", roleService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
            return "user/user";
        }
        return "redirect:/user/list";
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String username, RedirectAttributes redirectAttributes) {
        UserDto userDto = userService.delete(username);
        PaginationDto paginationDto = new PaginationDto();
        if (!userDto.isSuccess()) {
            model.addAttribute("message", userDto.getMessage());
            model.addAttribute("users", userService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
            return "user/list";
        }
        return "redirect:/user/list";
    }

    @PostMapping("/toggle-status")
    @ResponseBody
    public void toggle(Model model, @RequestParam String identifier) {
        userService.toggleStatus(identifier);
    }
}