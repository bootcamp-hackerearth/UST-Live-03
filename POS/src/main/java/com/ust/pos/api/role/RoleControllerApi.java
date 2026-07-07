package com.ust.pos.api.role;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Role;
import com.ust.pos.role.service.RoleService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleControllerApi extends BaseController {

    private final RoleService roleService;

    @PostMapping("/list")
    public PaginatedResponseDto<RoleDto> list(@RequestBody PaginationDto paginationDto) {
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
    public RoleDto addPost(@RequestBody RoleDto roleDto) {
        return roleService.save(roleDto);
    }

    @GetMapping("/get")
    public RoleDto get(@RequestParam String identifier) {
        return roleService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public RoleDto updatePost(@RequestBody RoleDto roleDto) {
        return roleService.update(roleDto);
    }

    @DeleteMapping("/delete")
    public RoleDto delete(@RequestBody RoleDto roleDto) {
        try {
            return roleService.delete(roleDto.getIdentifier());
        } catch (Exception e) {
            RoleDto errorDto = new RoleDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Delete failed: " + e.getMessage());
            return errorDto;
        }
    }

    @PatchMapping("/toggle")
    public boolean changeStatus(@RequestBody RoleDto roleDto) {
        try {
            roleService.changeStatus(roleDto.getIdentifier(), roleDto.getStatus());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/active")
    public List<RoleDto> getActiveRoles() {
        return roleService.findAllActive();
    }
}