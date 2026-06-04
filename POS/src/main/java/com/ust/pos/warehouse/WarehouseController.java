package com.ust.pos.warehouse;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/warehouse")
public class WarehouseController {

    public static final String REDIRECT_WAREHOUSE_LIST = "redirect:/warehouse/list";
    public static final String WAREHOUSES = "warehouses";
    @Autowired
    WarehouseService warehouseService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(WAREHOUSES, warehouseService.findAll(pageable));
        return "warehouse/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute WarehouseDto warehouseDto) {
        return "warehouse/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, Pageable pageable, @ModelAttribute WarehouseDto warehouseDto) {
        WarehouseDto response = warehouseService.save(warehouseDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(WAREHOUSES, warehouseService.findAll(pageable));
            return "warehouse/add";
        }
        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, Pageable pageable, @RequestParam String identifier, @ModelAttribute WarehouseDto warehouseDto) {
        WarehouseDto response = warehouseService.findByIdentifier(identifier);
        model.addAttribute(WAREHOUSES, warehouseService.findAll(pageable));
        model.addAttribute("warehouse", response);
        return "warehouse/warehouse";
    }

    @PostMapping("/update")
    public String updatePost(Model model, Pageable pageable, @ModelAttribute WarehouseDto warehouseDto) {
        WarehouseDto response = warehouseService.update(warehouseDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("warehouse", warehouseService.findAll(pageable));
        }
        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        warehouseService.delete(identifier);
        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        warehouseService.toggleStatus(identifier);
        return REDIRECT_WAREHOUSE_LIST;
    }
}
