package com.ust.pos.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/warehouse")
public class WarehouseController extends BaseController {

    public static final String MESSAGE = "message";
    private static final String WAREHOUSE_LIST = "warehouse/list";
    private static final String WAREHOUSE_ADD = "warehouse/add";
    private static final String WAREHOUSE_VIEW = "warehouse/warehouse";
    private static final String REDIRECT_WAREHOUSE_ADD = "redirect:/warehouse/add";
    private static final String REDIRECT_WAREHOUSE_LIST = "redirect:/warehouse/list";
    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/list")
    public String list(Model model, @ModelAttribute PaginationDto paginationDto) {
        model.addAttribute("warehouses", warehouseService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return WAREHOUSE_LIST;
    }

    @GetMapping("/add")
    public String add(@ModelAttribute WarehouseDto warehouseDto, Model model) {
        return WAREHOUSE_ADD;
    }

    @PostMapping("/add")
    public String addPost(RedirectAttributes redirectAttributes, @ModelAttribute WarehouseDto warehouseDto) {
        WarehouseDto warehouseDto1 = warehouseService.save(warehouseDto);
        redirectAttributes.addFlashAttribute(MESSAGE, warehouseDto1.getMessage());
        if (!warehouseDto1.isSuccess()) {
            return REDIRECT_WAREHOUSE_ADD;
        } else {
            return REDIRECT_WAREHOUSE_LIST;
        }
    }

    @PostMapping("/toggle")
    @ResponseBody
    public WarehouseDto toggleStatus(@RequestBody WarehouseDto dto) {
        return warehouseService.updateStatus(dto.getIdentifier(), dto.isStatus());
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        model.addAttribute("warehouseDto", warehouseService.findByIdentifier(identifier));
        return WAREHOUSE_VIEW;
    }

    @PostMapping("/update")
    public String updatePost(RedirectAttributes redirectAttributes, @ModelAttribute WarehouseDto warehouseDto) {
        WarehouseDto warehouseDto1 = warehouseService.update(warehouseDto);
        redirectAttributes.addFlashAttribute(MESSAGE, warehouseDto1.getMessage());
        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/delete")
    public String delete(RedirectAttributes redirectAttributes, @RequestParam String identifier) {
        warehouseService.delete(identifier);
        redirectAttributes.addFlashAttribute(MESSAGE, "Delete success");

        return REDIRECT_WAREHOUSE_LIST;
    }
}
