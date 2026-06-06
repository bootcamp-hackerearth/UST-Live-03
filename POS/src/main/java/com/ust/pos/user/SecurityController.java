package com.ust.pos.user;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.role.service.RoleService;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SecurityController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/register")
    public String add(Model model, @ModelAttribute UserDto userDto) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("roles", roleService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "register";
    }

    @PostMapping("/register")
    public String addPost(Model model, @ModelAttribute UserDto userDto, RedirectAttributes redirectAttributes) {
        UserDto response = userService.save(userDto);
        PaginationDto paginationDto = new PaginationDto();
        if (!response.isSuccess()) {
            model.addAttribute("roles", roleService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                    paginationDto.getSortDirection(), paginationDto.getSortField())));
            model.addAttribute("message", response.getMessage());
            return "register";
        }
        redirectAttributes.addFlashAttribute("message", "Register Success, Please login");
        return "redirect:/login";
    }


}
