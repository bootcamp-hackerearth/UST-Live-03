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

    public static final String REDIRECT_LIST = "redirect:/warehouse/list";
    public static final String WAREHOUSES = "warehouses";

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute(WAREHOUSES, warehouseService.findAll(pageable));
        return "warehouse/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(WAREHOUSES, new WarehouseDto());
        return "warehouse/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute WarehouseDto warehouseDto) {
        WarehouseDto response = warehouseService.save(warehouseDto);
        if (!response.isSuccess()) {
            model.addAttribute(WAREHOUSES, warehouseDto);
            model.addAttribute("message", response.getMessage());
            return "warehouse/add";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        model.addAttribute(WAREHOUSES, warehouseService.findByIdentifier(identifier));
        return "warehouse/edit";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute WarehouseDto warehouseDto) {
        WarehouseDto response = warehouseService.update(warehouseDto);
        if (!response.isSuccess()) {
            model.addAttribute(WAREHOUSES, warehouseDto);
            model.addAttribute("message", response.getMessage());
            return "warehouse/edit";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        warehouseService.delete(identifier);
        return REDIRECT_LIST;
    }

    @PostMapping("/toggle")
    public String toggleWarehouse(@RequestParam String identifier) {
        warehouseService.toggleStatus(identifier);
        return REDIRECT_LIST;
    }
}