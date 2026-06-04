package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("warehouseApiController")
@RequestMapping("/api/warehouses")
public class WarehouseController extends BaseController {

    @Autowired
    private WarehouseService warehouseService;

    @PostMapping("/list")
    public ResponseEntity<List<WarehouseDto>> list(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        List<WarehouseDto> warehouses = warehouseService.findAll(pageable);

        return ResponseEntity.ok(warehouses);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<WarehouseDto> getByIdentifier(@PathVariable String identifier) {

        WarehouseDto response = warehouseService.findByIdentifier(identifier);

        if (response == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<WarehouseDto> create(@RequestBody WarehouseDto warehouseDto) {

        WarehouseDto response = warehouseService.save(warehouseDto);

        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{identifier}")
    public ResponseEntity<WarehouseDto> update(@PathVariable String identifier, @RequestBody WarehouseDto warehouseDto) {

        warehouseDto.setIdentifier(identifier);

        WarehouseDto response = warehouseService.update(warehouseDto);

        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete/{identifier}")
    public ResponseEntity<Boolean> delete(@PathVariable String identifier) {

        try {

            warehouseService.delete(identifier);

            return ResponseEntity.ok(true);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @PostMapping("/toggle/{identifier}")
    public ResponseEntity<Boolean> toggleStatus(@PathVariable String identifier) {

        try {

            warehouseService.toggleStatus(identifier);

            return ResponseEntity.ok(true);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<WarehouseDto>> getActiveWarehouses() {

        List<WarehouseDto> warehouses = warehouseService.findIfTrue();

        return ResponseEntity.ok(warehouses);
    }
}