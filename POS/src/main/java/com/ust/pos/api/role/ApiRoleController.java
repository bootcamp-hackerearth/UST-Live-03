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
public class ApiRoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/list")
    public WsDto<RoleDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
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

    @PostMapping("/update")
    public RoleDto updatePost(@RequestBody RoleDto userDto) {
        return roleService.update(userDto);
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

    @GetMapping("/findallactive")
    public List<RoleDto> findAllActive() {
        return roleService.findAllActive();
    }

    @PostMapping("/toggle-status")
    public RoleDto toggle(@RequestParam String identifier) {
        return  roleService.toggleStatus(identifier);
    }

}