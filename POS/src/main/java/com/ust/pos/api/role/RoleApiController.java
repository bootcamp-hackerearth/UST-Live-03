package com.ust.pos.api.role;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.role.service.RoleService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleApiController extends BaseController {

    private final RoleService roleService;

    public RoleApiController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/list")
    public WsDto<RoleDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        return roleService.findAll(pageable);
    }

    @PostMapping("/add")
    public RoleDto addPost(@RequestBody RoleDto roleDto) {

        return roleService.save(roleDto);
    }

    @GetMapping("/{identifier}")
    public RoleDto update(@PathVariable String identifier) {

        return roleService.findByIdentifier(identifier);
    }

    @PatchMapping("/toggle")
    public boolean toggleStatus(@RequestBody String identifier) {
        try {
            roleService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/getactive")
    public List<RoleDto> getActiveRoles() {

        return roleService.findActiveRoles();
    }

    @PutMapping("/update")
    public RoleDto updatePost(@RequestBody RoleDto roleDto) {

        return roleService.update(roleDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody RoleDto roleDto) {

        String identifier = roleDto.getIdentifier();

        try {
            roleService.delete(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
