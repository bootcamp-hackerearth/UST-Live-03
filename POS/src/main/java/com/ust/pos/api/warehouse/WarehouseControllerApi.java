package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseControllerApi extends BaseController {

    private final WarehouseService warehouseService;

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin')")
    public PaginatedResponseDto<WarehouseDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return warehouseService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin')")
    public WarehouseDto addPost(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.save(warehouseDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('Admin')")
    public WarehouseDto get(@RequestParam String identifier) {
        return warehouseService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Admin')")
    public WarehouseDto updatePost(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.update(warehouseDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Admin')")
    public WarehouseDto delete(@RequestBody WarehouseDto warehouseDto) {
        try {
            return warehouseService.delete(warehouseDto.getIdentifier());
        } catch (Exception e) {
            WarehouseDto errorDto = new WarehouseDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Delete failed: " + e.getMessage());
            return errorDto;
        }
    }

    @PatchMapping("/toggle")
    @PreAuthorize("hasAuthority('Admin')")
    public boolean changeStatus(@RequestBody WarehouseDto warehouseDto) {
        try {
            warehouseService.changeStatus(warehouseDto.getIdentifier(), warehouseDto.getStatus());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('Admin')")
    public List<WarehouseDto> findAllActive() {
        return warehouseService.findAllActive();
    }
}