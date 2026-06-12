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

    public static final String WAREHOUSES = "warehouses";
    public static final String WAREHOUSE_LIST = "warehouse/list";
    public static final String MESSAGE = "message";
    @Autowired
    WarehouseService warehouseService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(WAREHOUSES, warehouseService.findAll(pageable));
        return WAREHOUSE_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute WarehouseDto warehouseDto) {
        return "warehouse/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute WarehouseDto warehouseDto, Pageable pageable) {
        WarehouseDto response = warehouseService.save(warehouseDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Warehouse Added Successfully");
        }
        model.addAttribute(WAREHOUSES, warehouseService.findAll(pageable));
        return WAREHOUSE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        WarehouseDto response = warehouseService.findByIdentifier(identifier);
        model.addAttribute("warehouse", response);
        return "warehouse/warehouse";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute WarehouseDto warehouseDto, Pageable pageable) {
        WarehouseDto response = warehouseService.update(warehouseDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Updated Successfully");
        }
        model.addAttribute(WAREHOUSES, warehouseService.findAll(pageable));
        return WAREHOUSE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier, Pageable pageable) {
        warehouseService.delete(identifier);
        model.addAttribute(WAREHOUSES, warehouseService.findAll(pageable));
        model.addAttribute(MESSAGE, "Warehouse deleted successfully");
        return WAREHOUSE_LIST;
    }
}
