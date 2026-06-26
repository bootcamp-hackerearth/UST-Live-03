package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseControllerApi extends BaseController {
    public static final String REDIRECT_WAREHOUSE_LIST = "redirect:/warehouse/list";

    private final WarehouseService warehouseService;

    public WarehouseControllerApi(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('manager')")
    public WsDto<WarehouseDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),
                paginationDto.getSortField()
        );
        return warehouseService.findAll(pageable);
    }

    @PostMapping("/add")
    public WarehouseDto addPost(@RequestBody WarehouseDto warehouseDto) {
        return warehouseService.save(warehouseDto);
    }

    @GetMapping("/get")
    public WarehouseDto update(@RequestParam String identifier) {
        return warehouseService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public WarehouseDto updatePost(@RequestBody WarehouseDto warehouseDto) {
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

    @PatchMapping("/toggle")
    public String toggle(@RequestParam String identifier,
                         @RequestParam boolean status) {
        warehouseService.updateStatus(identifier, status);
        return "success";
    }

    @PostMapping("/list-active")
    public List<WarehouseDto> listActive() {
        return warehouseService.findAllActive();
    }
}