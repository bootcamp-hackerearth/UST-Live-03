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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class ApiRoleController extends BaseController {

    private final RoleService roleService;

    public ApiRoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public WsDto<RoleDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable=getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Role> example = buildGlobalSearchSpec(Role.class, paginationDto.getKeyword());
            if (example != null) {
                return roleService.findAll(example, pageable);
            }
        }
        return roleService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RoleDto addPost(@RequestBody RoleDto userDto) {
        return roleService.save(userDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RoleDto update(@RequestParam String identifier) {
        return roleService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RoleDto updatePost(@RequestBody RoleDto roleDto) {
        return roleService.update(roleDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public boolean delete(@RequestParam String identifier) {
        try {
            roleService.delete(identifier);
        }
        catch (Exception e){
            return false;
        }
        return true;
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RoleDto toggle(@RequestBody RoleDto roleDto) {
        return roleService.changeToggleStatus(roleDto.getIdentifier(), roleDto.isStatus());
    }

    @GetMapping("/findActiveStatus")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<RoleDto> findActive() {
        return roleService.findActiveStatus();
    }
}
