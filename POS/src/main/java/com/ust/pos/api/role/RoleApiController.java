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
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage()
                , paginationDto.getSortDirection(), paginationDto.getSortField());
        return roleService.findAll(pageable);
    }

    @GetMapping("getAllActive")
    public List<RoleDto> listAllActive() {
        return (roleService.findAllActive());
    }

    @PostMapping("/add")
    public RoleDto add(@RequestBody RoleDto roleDto) {
        return roleService.save(roleDto);
    }

    @GetMapping("/get")
    public RoleDto getByIdentifier(@RequestParam String identifier) {
        return roleService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public RoleDto update(@RequestBody RoleDto roleDto) {
        return roleService.update(roleDto);
    }

    @GetMapping("/delete")
    public Boolean delete(@RequestParam String identifier) {
        try {
            roleService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle")
    public RoleDto toggleStatus(@RequestParam String identifier) {
        return roleService.toggleStatus(identifier);
    }
}
