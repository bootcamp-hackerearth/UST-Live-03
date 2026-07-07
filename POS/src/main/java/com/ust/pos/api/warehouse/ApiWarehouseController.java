package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Warehouse;
import com.ust.pos.warehouse.service.WarehouseService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class ApiWarehouseController extends BaseController {

    private final WarehouseService warehouseService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Developer')")
    public WarehouseDto addPost(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.save(warehouseDto);
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Developer')")
    public WsDto<WarehouseDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Warehouse> example = buildGlobalSearchSpec(Warehouse.class, paginationDto.getKeyword());
            if (example != null) {
                return warehouseService.findAll(example, pageable);
            }
        }
        return warehouseService.findAll(pageable);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('Developer')")
    public WarehouseDto update(@RequestParam String identifier) {
        return warehouseService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Developer')")
    public WarehouseDto updatePost(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.update(warehouseDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Developer')")
    public boolean delete(@RequestParam String identifier) {
        try {
            warehouseService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PatchMapping("/toggle-status")
    @PreAuthorize("hasAuthority('Developer')")
    public WarehouseDto toggle(@RequestParam String identifier) {
        return warehouseService.toggleStatus(identifier);
    }

    @GetMapping("/findallactive")
    @PreAuthorize("hasAuthority('Developer')")
    public List<WarehouseDto> findAllActive() {
        return warehouseService.findAllActive();
    }
}

