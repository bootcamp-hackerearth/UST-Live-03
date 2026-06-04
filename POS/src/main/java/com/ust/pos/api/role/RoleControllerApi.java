package com.ust.pos.api.role;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleControllerApi extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/role/list";
    @Autowired
    private RoleService roleService;

    @PostMapping("/list")
    public List<RoleDto> list(@RequestBody PaginationDto pagination) {
        Pageable pageable = getPageable(pagination.getPage(), pagination.getSizePerPage(),
                pagination.getSortDirection(), pagination.getSortfield());
        return roleService.findAll(pageable);
    }

    @PostMapping("/add")
    public RoleDto addPost(@RequestBody RoleDto roleDto) {
        return roleService.save(roleDto);
    }

    @GetMapping("/get")
    public RoleDto updatePage(@RequestParam String identifier) {
        return roleService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public RoleDto updatePost(@RequestBody RoleDto roleDto) {
        return roleService.update(roleDto);
    }

    @PostMapping("/delete")
    public RoleDto delete(@RequestBody RoleDto roleDto) {
        RoleDto response = new RoleDto();
        try {
            roleService.delete(roleDto.getIdentifier());
            response.setSuccess(true);
            response.setMessage("Role deleted successfully");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Delete failed");
        }
        return response;
    }
}