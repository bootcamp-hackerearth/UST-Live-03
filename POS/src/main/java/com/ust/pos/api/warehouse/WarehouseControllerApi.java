package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.warehouse.service.WarehouseService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseControllerApi extends BaseController {

    private final WarehouseService warehouseService;

    public WarehouseControllerApi(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }


    @PostMapping("/list")
    public WsDto<WarehouseDto> listWarehouses(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Warehouse> example = buildGlobalSearchSpec(Warehouse.class, paginationDto.getKeyword());
            if (example != null) {
                return warehouseService.findAll(example, pageable);
            }
        }
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

    @PutMapping("/update")
    public WarehouseDto updateWarehouse(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.update(warehouseDto);
    }

    @DeleteMapping("/delete")
    public boolean deleteWarehouse(@RequestParam String identifier) {
        try {
            warehouseService.delete(identifier);
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