package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseControllerApi extends BaseController {
    @Autowired
    WarehouseService warehouseService;

    @PostMapping("/list")
    public List<WarehouseDto> listWarehouses(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return warehouseService.findAll(pageable);
    }

    @PostMapping("/add")
    public WarehouseDto saveWarehouse(@RequestBody WarehouseDto warehouseDto) {

        return warehouseService.save(warehouseDto);
    }

    @GetMapping("/update")
    public WarehouseDto update(@RequestParam Long id) {
        return warehouseService.findById(id);
    }

    @PostMapping("/update")
    public WarehouseDto updateWarehouse(@RequestBody WarehouseDto warehouseDto) {

        return warehouseService.update(warehouseDto);
    }

    @GetMapping("/delete")
    public boolean deleteWarehouse(@RequestParam Long id) {
        try {
            warehouseService.delete(id);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/findAllActiveWarehouse")
    public List<WarehouseDto> allActiveWarehouse() {
        return warehouseService.findAllActiveWarehouse();
    }

    @PostMapping("/changeStatus")
    public WarehouseDto changestatus(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.changeWarehouseStatus(warehouseDto.getIdentifier(), warehouseDto.isStatus());
    }
}