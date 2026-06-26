package com.ust.pos.warehouse;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/warehouse")
public class WarehouseController {

    public static final String REDIRECT_WAREHOUSE_LIST = "redirect:/warehouse/list";

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute("warehouses", warehouseService.findAll());
        return "warehouse/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute WarehouseDto warehouseDto) {
        return "warehouse/add";
    }

    @PostMapping("/add")
    public String doadd(Model model, @ModelAttribute WarehouseDto warehouseDto) {
        WarehouseDto response = warehouseService.save(warehouseDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "warehouse/add";
        }

        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        WarehouseDto warehouseDto = warehouseService.findByIdentifier(identifier);
        model.addAttribute("warehouse", warehouseDto);
        return "warehouse/warehouse";
    }

    @PutMapping("/update")
    public String doupdate(Model model, @ModelAttribute WarehouseDto warehouseDto) {
        WarehouseDto response = warehouseService.update(warehouseDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("warehouse", warehouseDto);
            return "warehouse/warehouse";
        }

        return REDIRECT_WAREHOUSE_LIST;
    }

    @DeleteMapping("/delete")
    public String delete(@RequestParam String identifier) {
        warehouseService.delete(identifier);
        return REDIRECT_WAREHOUSE_LIST;
    }
}