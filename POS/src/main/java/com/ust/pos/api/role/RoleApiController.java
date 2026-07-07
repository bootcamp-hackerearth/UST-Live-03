package com.ust.pos.api.role;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.Role;
import com.ust.pos.role.service.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
public class RoleApiController extends BaseController {

    private final RoleService roleService;

    public RoleApiController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Manager')")
    public WsDto<RoleDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Role> example = buildGlobalSearchSpec(Role.class, paginationDto.getKeyword());
            if (example != null) {
                return roleService.findAll(example, pageable, paginationDto.getKeyword());
            }
        }
        return roleService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Manager')")
    public RoleDto addPost(@RequestBody RoleDto roleDto) {
        return roleService.save(roleDto);
    }

    @GetMapping("/get")
    public RoleDto update(@RequestParam String identifier) {
        return roleService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Manager')")
    public RoleDto updatePost(@RequestBody RoleDto roleDto) {
        return roleService.update(roleDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Manager')")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            roleService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
