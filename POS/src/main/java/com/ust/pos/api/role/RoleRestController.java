package com.ust.pos.api.role;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import com.ust.pos.role.service.RoleService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
public class RoleRestController extends BaseController {

    public static final String MESSAGE = "message";
    public static final String MESSAGE1 = "message";
    public static final String MESSAGE2 = "message";

    private final RoleService roleService;

    public RoleRestController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/list")
    public WsDto<RoleDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Role> example = buildGlobalSearchSpec(Role.class, paginationDto.getKeyword());
            if (example != null) {
                return roleService.findAll(example, pageable);
            }
        }

        return roleService.findAll(pageable);
    }

    @PostMapping("/add")
    public RoleDto addPost(@RequestBody RoleDto userDto) {

        return roleService.save(userDto);
    }

    @GetMapping("/get")
    public RoleDto update(@RequestParam String identifier) {
        return roleService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public RoleDto updatePost(@RequestBody RoleDto roleDto) {
        return roleService.update(roleDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            roleService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PutMapping("/toggle-status")
    public boolean toggleStatus(@RequestParam String identifier) {
        try {
            roleService.toggleStatus(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
