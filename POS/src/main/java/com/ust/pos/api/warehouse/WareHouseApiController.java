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
public class WareHouseApiController extends BaseController {

    private final WarehouseService warehouseService;

    public WareHouseApiController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public WsDto<WarehouseDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getSearch())) {
            Specification<Warehouse> example = buildGlobalSearchSpec(Warehouse.class, paginationDto.getSearch());
            if (example != null) {
                return warehouseService.findAll(example, pageable);
            }
        }
        return warehouseService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin')")
    public WarehouseDto addPost(@RequestBody WarehouseDto warehouseDto) {

        return warehouseService.save(warehouseDto);
    }

    @GetMapping("/{identifier}")
    @PreAuthorize("hasAuthority('Admin')")
    public WarehouseDto update(@PathVariable String identifier) {

        return warehouseService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Admin')")
    public WarehouseDto updatePost(@RequestBody WarehouseDto warehouseDto) {

        return warehouseService.update(warehouseDto);
    }

    @GetMapping("/getactive")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public List<WarehouseDto> getActiveWarehouses() {

        return warehouseService.findActiveWarehouse();
    }

    @PatchMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public boolean toggleStatus(@RequestBody String identifier) {

        try {
            warehouseService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Admin')")
    public boolean delete(@RequestBody WarehouseDto warehouseDto) {

        String identifier = warehouseDto.getIdentifier();

        try {
            warehouseService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}

