package com.ust.pos.api.role;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import com.ust.pos.role.service.RoleService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/role")
@PreAuthorize("hasAnyAuthority('Admin')")
public class RoleApiController extends BaseController {

    private final RoleService roleService;

    public RoleApiController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/list")
    public WsDto<RoleDto> home(@RequestBody PaginationDto paginationDto) throws Exception {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        Example<Role> example = buildSearchProbe(Role.class, paginationDto.getSearch());
        Page<RoleDto> pageResult = roleService.findAll(example, pageable);
        WsDto<RoleDto> response = new WsDto<>();

        response.setDtoList(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPage(pageResult.getTotalPages());
        response.setTotalRecords(pageResult.getTotalElements());

        return response;
    }

    @PostMapping("/add")
    public RoleDto addPost(@RequestBody RoleDto userDto) {
        return roleService.save(userDto);
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
    public Boolean delete(@RequestParam String identifier) {
        try{
            roleService.delete(identifier);
        }catch(Exception e)
        {
            return false;
        }
        return true;
    }
}
