package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseControllerApi extends BaseController {

    @Autowired
    WarehouseService warehouseService;

    @PostMapping("/list")
    public WsDto<WarehouseDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return warehouseService.findAll(pageable);
    }

    @PostMapping("/add")
    public WarehouseDto addPost(Model model, @RequestBody WarehouseDto warehouseDto) {
        return warehouseService.save(warehouseDto);
    }

    @GetMapping("/get")
    public WarehouseDto update(Model model, @RequestParam String identifier, @RequestBody WarehouseDto warehouseDto) {
        return warehouseService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public WarehouseDto updatePost(Model model, @RequestBody WarehouseDto warehouseDto) {
        return warehouseService.update(warehouseDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            warehouseService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
