package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.warehouse.service.WareHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
public class WareHouseApiController extends BaseController {

    @Autowired
    private WareHouseService warehouseService;

    @PostMapping("/list")
    public WsDto<WareHouseDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return warehouseService.findAll(pageable);
    }

    @PostMapping("/add")
    public WareHouseDto add(@RequestBody WareHouseDto wareHouseDto) {
        return warehouseService.save(wareHouseDto);
    }

    @GetMapping("/get")
    public WareHouseDto get(@RequestParam String identifier) {
        return warehouseService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public WareHouseDto update(@RequestBody WareHouseDto wareHouseDto) {
        return warehouseService.update(wareHouseDto);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody WareHouseDto wareHouseDto) {
        try {
            warehouseService.delete(wareHouseDto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/warehouse")
    public List<WareHouseDto> getActiveWareHouse() {
        return warehouseService.findActiveWareHouse();
    }

    @PostMapping("/toggleStatus")
    public WareHouseDto toggleStatus(@RequestBody WareHouseDto wareHouseDto) {
        return warehouseService.toggleStatus(wareHouseDto.getIdentifier(), wareHouseDto.isStatus());
    }
}