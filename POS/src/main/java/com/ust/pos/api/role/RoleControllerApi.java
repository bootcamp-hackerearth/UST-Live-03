package com.ust.pos.api.role;
import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RoleDto;
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
public class RoleControllerApi extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/role/list";

    private final RoleService roleService;

    public RoleControllerApi(RoleService roleService){
        this.roleService=roleService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin')")
    public PageDto<RoleDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Role> spec = buildGlobalSearchSpec(Role.class, paginationDto.getKeyword());
            return roleService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return roleService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin')")
    public RoleDto addPost(@RequestBody RoleDto roleDto) {
        return roleService.save(roleDto);
    }

    @GetMapping("/get")
    public RoleDto update( @RequestParam String identifier) {
        return roleService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Admin')")
    public RoleDto updatePost(@RequestBody RoleDto roleDto) {
         return roleService.update(roleDto);

    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Admin')")
    public boolean delete(@RequestParam String identifier) {
       try {
           roleService.delete(identifier);
       }
       catch(Exception e){
           return false;
       }
       return true;
    }

    @PostMapping("/toggleStatus")
    @PreAuthorize("hasAuthority('Admin')")
    public void toggleStatus(@RequestParam String identifier) {
        roleService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<RoleDto> findByStatus() {
        return roleService.findActiveRoles();
    }
}
