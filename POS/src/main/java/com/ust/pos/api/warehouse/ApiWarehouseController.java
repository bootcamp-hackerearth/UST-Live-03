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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/warehouse")

public class ApiWarehouseController extends BaseController {

    private final WarehouseService warehouseService;

    public ApiWarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public WsDto<WarehouseDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Warehouse> example = buildGlobalSearchSpec(Warehouse.class, paginationDto.getKeyword());
            if (example != null) {
                return warehouseService.findAll(example, pageable);
            }
        }
        return warehouseService.findAll(pageable);
    }
    
    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public WarehouseDto addWarehouse(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.save(warehouseDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public boolean delete(@RequestParam String identifier) {
        try{
            warehouseService.delete(identifier);
        }
        catch (Exception e){
            return false;
        }
        return true;
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public WarehouseDto update(@RequestParam String identifier) {
        return warehouseService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public WarehouseDto updateWarehouse(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.update(warehouseDto);
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public WarehouseDto toggle(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.changeToggleStatus(warehouseDto.getIdentifier(), warehouseDto.isStatus());
    }

    @PostMapping("/findActiveStatus")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public List<WarehouseDto> findActive() {
        return warehouseService.findActiveStatus();
    }
}
