package com.ust.pos.api.role;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import com.ust.pos.role.service.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class ApiRoleController extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/role/list";

    private final RoleService roleService;

    public ApiRoleController(RoleService roleService) {
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
    public RoleDto addPost(@RequestBody RoleDto roleDto) {

        return roleService.save(roleDto);
    }

    @GetMapping("/get")
    public RoleDto update(@RequestParam String identifier) {

        return roleService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public RoleDto updatePost(@RequestBody RoleDto userDto) {

        return roleService.update(userDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            roleService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    public RoleDto toggle(@RequestParam String identifier) {

        return roleService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<RoleDto> findByStatus() {

        return roleService.findIfTrue();
    }

}
