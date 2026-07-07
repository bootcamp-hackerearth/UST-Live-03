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
public class WarehouseControllerApi extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/warehouse/list";

    private final WarehouseService warehouseService;

    public WarehouseControllerApi(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public WsDto<WarehouseDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortfield());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Warehouse> example = buildGlobalSearchSpec(Warehouse.class, paginationDto.getKeyword());
            if (example != null) {
                return warehouseService.findAll(example, pageable);
            }
        }
        return warehouseService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public WarehouseDto addPost(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.save(warehouseDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public WarehouseDto update(@RequestParam String identifier) {
        return warehouseService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public WarehouseDto updatePost(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.update(warehouseDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public WarehouseDto delete(@RequestBody WarehouseDto warehouseDto) {
        warehouseService.delete(warehouseDto.getIdentifier());
        WarehouseDto response = new WarehouseDto();
        response.setSuccess(true);
        response.setMessage("Warehouse deleted successfully");
        return response;
    }

    @PatchMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public WarehouseDto toggle(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.toggleStatus(warehouseDto.getIdentifier());
    }

    @GetMapping("/active")
    public List<WarehouseDto> getActiveWarehouses() {
        return warehouseService.findActiveWarehouses();
    }
}