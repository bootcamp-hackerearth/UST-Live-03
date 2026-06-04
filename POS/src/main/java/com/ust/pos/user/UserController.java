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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("users", userService.findAll(pageable));
        return "user/list";
    }

    @GetMapping("/get")
    public String updatePage(Model model, @RequestParam String username) {

        UserDto userDto = userService.findByUserName(username);

        PaginationDto paginationDto = new PaginationDto();
        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),
                paginationDto.getSortField()
        );

        model.addAttribute("userDto", userDto);
        model.addAttribute("users", userService.findAll(pageable));
        model.addAttribute("roles", roleService.findIfTrue());

        return "user/user";
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute("userDto") UserDto userDto,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        UserDto response = userService.update(userDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("roles", roleService.findIfTrue());
            return "user/user";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "User updated successfully."
        );

        return "redirect:/user/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String username,
                         RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUser = auth != null ? auth.getName() : null;

        userService.delete(username);

        if (loggedInUser != null && loggedInUser.equals(username)) {
            SecurityContextHolder.clearContext();
            redirectAttributes.addFlashAttribute(
                    "infoMessage",
                    "Your account was deleted successfully."
            );
            return "redirect:/login";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "User deleted successfully."
        );

        return "redirect:/user/list";
    }
}