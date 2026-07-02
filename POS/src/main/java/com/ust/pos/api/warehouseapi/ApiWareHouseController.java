package com.ust.pos.api.warehouseapi;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.warehouse.service.WareHouseService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wareHouse")
public class ApiWareHouseController extends BaseController {

    private final WareHouseService wareHouseService;

    public ApiWareHouseController(WareHouseService wareHouseService) {
        this.wareHouseService = wareHouseService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN','INVENTORY_MANAGER')")
    public WsDto<WareHouseDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return wareHouseService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN','INVENTORY_MANAGER')")
    public WareHouseDto addPost(@RequestBody WareHouseDto wareHouseDto) {
        return wareHouseService.save(wareHouseDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('ADMIN','INVENTORY_MANAGER')")
    public WareHouseDto update(@RequestParam String identifier) {
        return wareHouseService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN','INVENTORY_MANAGER')")
    public WareHouseDto updatePost(@RequestBody WareHouseDto wareHouseDto) {
        return wareHouseService.update(wareHouseDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN','INVENTORY_MANAGER')")
    public boolean delete(@RequestParam String identifier) {
        try {
            wareHouseService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    @PreAuthorize("hasAuthority('ADMIN','INVENTORY_MANAGER')")
    public WareHouseDto toggle(@RequestParam String identifier) {
        return wareHouseService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    @PreAuthorize("hasAuthority('ADMIN','INVENTORY_MANAGER')")
    public List<WareHouseDto> findByStatus() {
        return wareHouseService.findIfTrue();
    }
}