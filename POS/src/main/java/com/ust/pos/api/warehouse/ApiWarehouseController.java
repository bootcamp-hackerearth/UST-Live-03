package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
public class ApiWarehouseController extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/warehouse/list";
    @Autowired
    private WarehouseService warehouseService;

    @PostMapping("/list")
    public List<WarehouseDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
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

    @PostMapping("/update")
    public WarehouseDto updatePost(@RequestBody WarehouseDto warehousDto) {

        return warehouseService.update(warehousDto);
    }

    @GetMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            warehouseService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    public WarehouseDto toggle(@RequestParam String identifier) {

        return warehouseService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<WarehouseDto> findByStatus() {

        return warehouseService.findIfTrue();
    }
}
