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
        WarehouseDto warehouseDto1 = warehouseService.save(warehouseDto);
        if (!warehouseDto1.isSuccess()) {
            model.addAttribute("message", warehouseDto1.getMessage());
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
        WarehouseDto warehouseDto1 = warehouseService.update(warehouseDto);
        if (!warehouseDto1.isSuccess()) {
            model.addAttribute("message", warehouseDto1.getMessage());
            return "warehouse/warehouse";
        }
        return REDIRECT_WAREHOUSE_LIST;
    }

    @DeleteMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        warehouseService.delete(identifier);
        return REDIRECT_WAREHOUSE_LIST;
    }
}
