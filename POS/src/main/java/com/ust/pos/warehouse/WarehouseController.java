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
    public static final String WAREHOUSE = "warehouse";

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(WAREHOUSE, new WarehouseDto());
        return "warehouse/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute(WAREHOUSE) WarehouseDto warehouseDto) {
        WarehouseDto response = warehouseService.save(warehouseDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(WAREHOUSE, warehouseDto);
            return "warehouse/add";
        }

        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("warehouses", warehouseService.findAll(pageable));
        return "warehouse/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        WarehouseDto response = warehouseService.findByIdentifier(identifier);
        model.addAttribute(WAREHOUSE, response);
        return "warehouse/warehouse";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute WarehouseDto warehouseDto) {
        WarehouseDto response = warehouseService.update(warehouseDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "warehouse/warehouse";
        }

        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        warehouseService.delete(identifier);
        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        warehouseService.toggleStatus(identifier);
        return REDIRECT_WAREHOUSE_LIST;
    }
}