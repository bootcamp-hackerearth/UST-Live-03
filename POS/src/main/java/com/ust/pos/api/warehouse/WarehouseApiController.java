package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseApiController extends BaseController {

    @Autowired
    private WarehouseService warehouseService;

    @PostMapping("/list")
    public WsDto<WarehouseDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return warehouseService.findAll(pageable);
    }

    @PostMapping("/add")
    public WarehouseDto addPost(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.save(warehouseDto);
    }

    @PostMapping("/get")
    public WarehouseDto update(@RequestBody String identifier) {
        return warehouseService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public WarehouseDto updatePost(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.update(warehouseDto);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody String identifier) {
        try {
            warehouseService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
