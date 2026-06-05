package com.ust.pos.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/warehouse")
public class WarehouseController extends BaseController {

    public static final String REDIRECT_WAREHOUSE_LIST = "redirect:/warehouse/list";

    @Autowired
    WarehouseService warehouseService;

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute WarehouseDto warehouseDto) {
        return "warehouse/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute WarehouseDto warehouseDto) {
        WarehouseDto response = warehouseService.save(warehouseDto);
        if (!response.isSuccess()) {
            model.addAttribute("warehouses", warehouseService.findAll(null));
            model.addAttribute("message", response.getMessage());
            return "warehouse/add";
        }
        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, @ModelAttribute PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),paginationDto.getSortField());
        model.addAttribute("warehouses", warehouseService.findAll(pageable));
        return "warehouse/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        WarehouseDto response = warehouseService.findByIdentifier(identifier);
        model.addAttribute("warehouse", response);
        return "warehouse/warehouse";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute WarehouseDto warehouseDto) {
        WarehouseDto response = warehouseService.update(warehouseDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        warehouseService.delete(identifier);
        return REDIRECT_WAREHOUSE_LIST;
    }
}