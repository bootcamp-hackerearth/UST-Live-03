package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseControllerApi extends BaseController {

    private final WarehouseService warehouseService;

    public WarehouseControllerApi(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('Admin')")
    public List<WarehouseDto> all() {
        return warehouseService.findAll();
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin')")
    public WsDto<WarehouseDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(), paginationDto.getSortField());
        Page<WarehouseDto> pageResult = warehouseService.findAll(pageable, paginationDto.getSearch());
        WsDto<WarehouseDto> response = new WsDto<>();
        response.setContent(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPages(pageResult.getTotalPages());
        return response;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin')")
    public WarehouseDto addPost(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.save(warehouseDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('Admin')")
    public WarehouseDto update(@RequestParam String identifier) {
        return warehouseService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Admin')")
    public WarehouseDto updatePost(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.update(warehouseDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Admin')")
    public boolean delete(@RequestParam String identifier) {
        try {
            warehouseService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}