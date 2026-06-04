package com.ust.pos.warehouse;

import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.warehouse.service.WareHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/warehouse")
public class WareHouseController {

    public static final String REDIRECT_WAREHOUSE_LIST = "redirect:/warehouse/list";
    public static final String MESSAGE = "message";

    @Autowired
    private WareHouseService warehouseService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("warehouses", warehouseService.findAll(pageable));
        return "warehouse/list";
    }

    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("warehouseDto", new WareHouseDto());
        return "warehouse/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute("warehouseDto") WareHouseDto warehouseDto) {
        WareHouseDto response = warehouseService.save(warehouseDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            return "warehouse/add";
        }
        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/get")
    public String updatePage(Model model, @RequestParam String identifier) {
        WareHouseDto warehouseDto = warehouseService.findByIdentifier(identifier);
        if (warehouseDto == null) {
            model.addAttribute(MESSAGE, "WareHouse not found");
            return REDIRECT_WAREHOUSE_LIST;
        }
        model.addAttribute("warehouseDto", warehouseDto);
        return "warehouse/warehouse";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute("warehouseDto") WareHouseDto warehouseDto) {
        WareHouseDto response = warehouseService.update(warehouseDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            return "warehouse/warehouse";
        }
        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        warehouseService.delete(identifier);
        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/toggleStatus")
    public String toggleWareHouseStatus(@RequestParam String identifier, boolean status) {
        warehouseService.toggleStatus(identifier, status);
        return REDIRECT_WAREHOUSE_LIST;
    }
}