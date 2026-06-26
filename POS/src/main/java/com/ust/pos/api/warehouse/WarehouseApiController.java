package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseApiController extends BaseController {

    private final WarehouseService warehouseService;

    public WarehouseApiController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/list")
    public List<WarehouseDto> home() {
        return warehouseService.findAll();
    }

    @PostMapping("/list")
    public WsDto<WarehouseDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortField());
        Page<WarehouseDto> warehouseDtos = warehouseService.findAll(pageable, paginationDto.getSearch());
        WsDto<WarehouseDto> result = new WsDto<>();
        result.setTotalPages(warehouseDtos.getTotalPages());
        result.setContent(warehouseDtos.getContent());
        result.setSizePerPage(warehouseDtos.getSize());
        result.setPage(warehouseDtos.getNumber());
        return result;
    }

    @PostMapping("/add")
    public WarehouseDto doadd(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.save(warehouseDto);
    }

    @GetMapping("/get")
    public WarehouseDto update(@RequestParam String identifier) {
        return warehouseService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public WarehouseDto doupdate(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.update(warehouseDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            warehouseService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
