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

    public static final String WAREHOUSE_DTO = "warehouseDto";
    public static final String MESSAGE = "message";
    private static final String REDIRECT = "redirect:/warehouse/list";
    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("warehouses", warehouseService.findAll(pageable));
        return "warehouse/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(WAREHOUSE_DTO, new WarehouseDto());
        return "warehouse/add";
    }

    @PostMapping("/add")
    public String save(@ModelAttribute WarehouseDto warehouseDto, Model model) {
        WarehouseDto response = warehouseService.save(warehouseDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute(WAREHOUSE_DTO, warehouseDto);
            return "warehouse/add";
        }
        return REDIRECT;
    }

    @GetMapping("/get")
    public String edit(@RequestParam String identifier, Model model, Pageable pageable) {
        WarehouseDto response = warehouseService.findByIdentifier(identifier);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute("warehouses", warehouseService.findAll(pageable));
            return "warehouse/list";
        }
        model.addAttribute(WAREHOUSE_DTO, response);
        return "warehouse/warehouse";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute WarehouseDto warehouseDto, Model model) {
        WarehouseDto response = warehouseService.update(warehouseDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute(WAREHOUSE_DTO, warehouseDto);
            return "warehouse/warehouse";
        }
        return REDIRECT;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        warehouseService.delete(identifier);
        return REDIRECT;
    }
}