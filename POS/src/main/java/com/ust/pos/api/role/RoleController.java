package com.ust.pos.api.role;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("roleApiController")
@RequestMapping("/api/role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/list")
    public ResponseEntity<List<RoleDto>> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        List<RoleDto> roles = roleService.findAll(pageable);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<RoleDto> getByIdentifier(@PathVariable String identifier) {
        RoleDto response = roleService.findByIdentifier(identifier);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<RoleDto> save(@RequestBody RoleDto roleDto) {
        RoleDto response = roleService.save(roleDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{identifier}")
    public ResponseEntity<RoleDto> update(@PathVariable String identifier, @RequestBody RoleDto roleDto) {
        roleDto.setIdentifier(identifier);
        RoleDto response = roleService.update(roleDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete/{identifier}")
    public ResponseEntity<Boolean> delete(@PathVariable String identifier) {
        try {
            roleService.delete(identifier);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }
}