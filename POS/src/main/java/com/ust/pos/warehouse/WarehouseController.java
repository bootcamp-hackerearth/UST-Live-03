package com.ust.pos.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/warehouse")
public class WarehouseController extends BaseController {

    private static final String VIEW_WAREHOUSE_LIST = "warehouse/list";
    private static final String VIEW_WAREHOUSE_ADD = "warehouse/add";
    private static final String VIEW_WAREHOUSE_EDIT = "warehouse/warehouse";
    private static final String REDIRECT_WAREHOUSE_LIST = "redirect:/warehouse/list";

    private static final String ATTR_WAREHOUSE_DTO = "warehouseDto";
    private static final String ATTR_WAREHOUSES = "warehouses";
    private static final String ATTR_MESSAGE = "message";
    private static final String ATTR_MESSAGE_TYPE = "messageType";

    private static final String MESSAGE_TYPE_ERROR = "error";

    private static final String MSG_WAREHOUSE_NOT_FOUND = "Warehouse not found";

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/list")
    public String home(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute(ATTR_WAREHOUSES, warehouseService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
        return VIEW_WAREHOUSE_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute WarehouseDto warehouseDto) {
        model.addAttribute(ATTR_WAREHOUSE_DTO, warehouseDto);
        return VIEW_WAREHOUSE_ADD;
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute WarehouseDto warehouseDto) {

        WarehouseDto response = warehouseService.save(warehouseDto);

        if (!response.isSuccess()) {
            model.addAttribute(ATTR_WAREHOUSE_DTO, warehouseDto);
            model.addAttribute(ATTR_MESSAGE, response.getMessage());
            model.addAttribute(ATTR_MESSAGE_TYPE, MESSAGE_TYPE_ERROR);
            return VIEW_WAREHOUSE_ADD;
        }

        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {

        WarehouseDto response = warehouseService.findByIdentifier(identifier);

        if (response == null) {
            model.addAttribute(ATTR_MESSAGE, MSG_WAREHOUSE_NOT_FOUND);
            model.addAttribute(ATTR_MESSAGE_TYPE, MESSAGE_TYPE_ERROR);
            return REDIRECT_WAREHOUSE_LIST;
        }

        model.addAttribute(ATTR_WAREHOUSE_DTO, response);
        return VIEW_WAREHOUSE_EDIT;
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute WarehouseDto warehouseDto) {

        WarehouseDto response = warehouseService.update(warehouseDto);

        if (!response.isSuccess()) {
            model.addAttribute(ATTR_WAREHOUSE_DTO, warehouseDto);
            model.addAttribute(ATTR_MESSAGE, response.getMessage());
            model.addAttribute(ATTR_MESSAGE_TYPE, MESSAGE_TYPE_ERROR);
            return VIEW_WAREHOUSE_EDIT;
        }

        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        warehouseService.delete(identifier);
        return REDIRECT_WAREHOUSE_LIST;
    }

    @PostMapping("/toggle-status")
    @ResponseBody
    public void toggleStatus(@RequestParam String identifier) {
        warehouseService.toggleStatus(identifier);
    }
}