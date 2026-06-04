package com.ust.pos.api.role;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleRestController extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/role/list";
    public static final String ROLE_ADD = "role/add";
    @Autowired
    private RoleService roleService;

    @PostMapping("/list")
    public List<RoleDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return roleService.findAll(pageable);
    }

    @PostMapping("/add")
    public RoleDto addPost(@RequestBody RoleDto roleDto) {
        return roleService.save(roleDto);
    }

    @GetMapping("/get")
    public RoleDto update(@RequestParam String identifier) {
        return roleService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public RoleDto updatePost(@RequestBody RoleDto roleDto) {
        return roleService.update(roleDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            roleService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
