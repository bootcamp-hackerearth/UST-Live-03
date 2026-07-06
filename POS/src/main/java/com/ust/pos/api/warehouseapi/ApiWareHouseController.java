package com.ust.pos.api.warehouseapi;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.WareHouse;
import com.ust.pos.warehouse.service.WareHouseService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN','AUDITOR')")
    public WsDto<WareHouseDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<WareHouse> example = buildGlobalSearchSpec(WareHouse.class, paginationDto.getKeyword());
            if (example != null) {
                return wareHouseService.findAll(example, pageable, paginationDto.getKeyword());
            }
        }
        return wareHouseService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN')")
    public WareHouseDto addPost(@RequestBody WareHouseDto wareHouseDto) {
        return wareHouseService.save(wareHouseDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN')")
    public WareHouseDto update(@RequestParam String identifier) {
        return wareHouseService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN')")
    public WareHouseDto updatePost(@RequestBody WareHouseDto wareHouseDto) {
        return wareHouseService.update(wareHouseDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN')")
    public boolean delete(@RequestParam String identifier) {
        try {
            wareHouseService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN')")
    public WareHouseDto toggle(@RequestParam String identifier) {
        return wareHouseService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN','AUDITOR')")
    public List<WareHouseDto> findByStatus() {
        return wareHouseService.findIfTrue();
    }
}