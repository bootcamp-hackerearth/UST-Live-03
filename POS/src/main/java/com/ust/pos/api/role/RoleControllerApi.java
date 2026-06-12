package com.ust.pos.api.role;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleControllerApi extends BaseController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/list")
    public PaginatedResponseDto<RoleDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
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

    @PostMapping("/delete")
    public boolean delete(@RequestBody RoleDto roleDto) {
        try {
            roleService.delete(roleDto.getIdentifier());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle")
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
