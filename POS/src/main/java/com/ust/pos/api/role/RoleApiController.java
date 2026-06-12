package com.ust.pos.api.role;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleApiController extends BaseController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/list")
    public WsDto<RoleDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return roleService.findAll(pageable);
    }

    @PostMapping("/add")
    public RoleDto add(@RequestBody RoleDto roleDto) {
        return roleService.save(roleDto);
    }

    @GetMapping("/get")
    public RoleDto get(@RequestParam String identifier) {
        return roleService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public RoleDto update(@RequestBody RoleDto roleDto) {
        return roleService.update(roleDto);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody RoleDto roleDto) {
        try {
            roleService.delete(roleDto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping
    public List<RoleDto> getRoles() {
        return roleService.findActiveRole();
    }

    @PostMapping("/toggleStatus")
    public RoleDto toggleStatus(@RequestBody RoleDto roleDto) {
        return roleService.toggleStatus(roleDto.getIdentifier(), roleDto.isStatus());
    }
}