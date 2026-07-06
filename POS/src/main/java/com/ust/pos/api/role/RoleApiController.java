package com.ust.pos.api.role;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Role;
import com.ust.pos.role.service.RoleService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
public class RoleApiController extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/role/list";

    private final RoleService roleService;

    public RoleApiController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Admin','Seller')")
    public PaginationResponseDto<RoleDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Role> example = buildGlobalSearchSpec(Role.class, paginationDto.getKeyword());
            if (example != null) {
                return roleService.findAll(example, pageable);
            }
        }
        return roleService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin')")
    public RoleDto addPost(@RequestBody RoleDto userDto) {
        return roleService.save(userDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('Seller')")
    public RoleDto update(@RequestParam String identifier) {
        return roleService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Seller')")
    public RoleDto updatePost(@RequestBody RoleDto userDto) {
        return roleService.update(userDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Seller')")
    public boolean delete(@RequestParam String identifier) {
        try {
            roleService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    @PreAuthorize("hasAuthority('Seller')")
    public RoleDto toggle(@RequestBody RoleDto dto) {
        return roleService.toggleStatus(dto.getIdentifier(), dto.isStatus());
    }
}