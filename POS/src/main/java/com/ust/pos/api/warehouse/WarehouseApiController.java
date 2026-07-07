package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/warehouse")
@PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
public class WarehouseApiController extends BaseController {

    private final WarehouseService warehouseService;

    public WarehouseApiController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping("/list")
    public WsDto<WarehouseDto> home(@RequestBody PaginationDto paginationDto) throws Exception
    {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        Example<Warehouse> example = buildSearchProbe(Warehouse.class, paginationDto.getSearch(), "pincode");
        Page<WarehouseDto> pageResult = warehouseService.findAll(example, pageable);
        WsDto<WarehouseDto> response = new WsDto<>();

        response.setDtoList(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPage(pageResult.getTotalPages());
        response.setTotalRecords(pageResult.getTotalElements());

        return response;
    }
    @GetMapping("/toggleStatus")
    public Boolean toggleStatus(@RequestParam String identifier)
    {
        try
        {
            warehouseService.toggleStatus(identifier);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    @PostMapping("/add")
    public WarehouseDto doadd(@RequestBody WarehouseDto warehouseDto)
    {
        return warehouseService.save(warehouseDto);
    }

    @GetMapping("/get")
    public WarehouseDto update(@RequestParam String identifier)
    {
        return warehouseService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public WarehouseDto doupdate(@RequestBody WarehouseDto warehouseDto)
    {
        return warehouseService.update(warehouseDto);
    }

    @DeleteMapping("/delete")
    public Boolean delete(Model model, @RequestParam String identifier)
    {
        try {
            warehouseService.delete(identifier);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
}
