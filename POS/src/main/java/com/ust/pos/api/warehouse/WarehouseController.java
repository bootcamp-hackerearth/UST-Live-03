package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("warehouseApiController")
@RequestMapping("/api/warehouses")
public class WarehouseController extends BaseController {
    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<List<WarehouseDto>> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        List<WarehouseDto> warehouses = warehouseService.findAll(pageable);
        return ResponseEntity.ok(warehouses);
    }

    @GetMapping("/{identifier}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<WarehouseDto> getByIdentifier(@PathVariable String identifier) {
        WarehouseDto response = warehouseService.findByIdentifier(identifier);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<WarehouseDto> create(@RequestBody WarehouseDto warehouseDto) {
        WarehouseDto response = warehouseService.save(warehouseDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{identifier}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<WarehouseDto> update(@PathVariable String identifier, @RequestBody WarehouseDto warehouseDto) {
        warehouseDto.setIdentifier(identifier);
        WarehouseDto response = warehouseService.update(warehouseDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{identifier}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Boolean> delete(@PathVariable String identifier) {
        try {
            warehouseService.delete(identifier);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @PostMapping("/toggle/{identifier}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Boolean> toggleStatus(@PathVariable String identifier) {
        try {
            warehouseService.toggleStatus(identifier);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<List<WarehouseDto>> getActiveWarehouses() {
        List<WarehouseDto> warehouses = warehouseService.findIfTrue();
        return ResponseEntity.ok(warehouses);
    }
}