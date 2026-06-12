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
    private static final String MESSAGE = "message";

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/add")
    public String add() {
        return "warehouse/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute WarehouseDto warehouseDto) {
        WarehouseDto response = warehouseService.save(warehouseDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            return "redirect:/warehouse/add";
        }
        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
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
    public String updatePost(@ModelAttribute WarehouseDto warehouseDto) {
        WarehouseDto response = warehouseService.update(warehouseDto);
        if (!response.isSuccess()) {
            return REDIRECT_WAREHOUSE_LIST;
        }
        return "redirect:/warehouse/get?identifier=" + warehouseDto.getIdentifier();
    }

    @PostMapping("/toggle")
    @ResponseBody
    public void toggleStatus(@RequestParam String identifier) {
        warehouseService.toggleStatus(identifier);
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        warehouseService.delete(identifier);
        return REDIRECT_WAREHOUSE_LIST;
    }
}
