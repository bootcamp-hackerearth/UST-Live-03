package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseControllerApi extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/warehouse/list";

    private final WarehouseService warehouseService;

    public WarehouseControllerApi(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public WsDto<WarehouseDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortfield());
        return warehouseService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public WarehouseDto addPost(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.save(warehouseDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public WarehouseDto update(@RequestParam String identifier) {
        return warehouseService.findByIdentifier(identifier);
    }
    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public WarehouseDto updatePost(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.update(warehouseDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public Boolean delete(@RequestBody WarehouseDto warehouseDto) {
        try {
            warehouseService.delete(warehouseDto.getIdentifier());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}