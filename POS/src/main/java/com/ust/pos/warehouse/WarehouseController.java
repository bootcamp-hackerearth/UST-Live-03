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

    @GetMapping("/list")
    public String listWarehouses(Model model, Pageable pageable) {
        model.addAttribute("warehouseList", warehouseService.findAll(pageable));
        return "warehouse/list";
    }

    @Autowired
    WarehouseService warehouseService;

    @GetMapping("/add")
    public String showAddPage(Model model) {
        model.addAttribute("warehouseDto", new WarehouseDto());
        return "warehouse/add";
    }

    @PostMapping("/add")
    public String saveWarehouse(Model model, @ModelAttribute WarehouseDto warehouseDto) {

        WarehouseDto response = warehouseService.save(warehouseDto);
        model.addAttribute("success", response.isStatus());
        model.addAttribute("message", response.getMessage());
        model.addAttribute("warehouseDto", new WarehouseDto());
        return REDIRECT_WAREHOUSE_LIST;
    }


    @GetMapping("/update")
    public String update(Model model, @RequestParam Long id) {
        WarehouseDto response = warehouseService.findById(id);
        model.addAttribute("warehouse", response);
        return "warehouse/warehouse";
    }

    @PostMapping("/update")
    public String updateWarehouse(@ModelAttribute WarehouseDto warehouse) {
        warehouseService.update(warehouse);
        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/delete")
    public String deleteWarehouse(@RequestParam Long id) {
        warehouseService.delete(id);
        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status) {
        warehouseService.changeWarehouseStatus(identifier, status);
        return REDIRECT_WAREHOUSE_LIST;
    }
}