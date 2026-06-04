package com.ust.pos.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.warehouse.service.WareHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/wareHouse")
public class WareHouseController extends BaseController {

    public static final String REDIRECT_WAREHOUSE_LIST = "redirect:/wareHouse/list";

    @Autowired
    private WareHouseService wareHouseService;

    @GetMapping("/list")
    public String home(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("wareHouses", wareHouseService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "warehouse/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute WareHouseDto wareHouseDto) {
        return "warehouse/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute WareHouseDto wareHouseDto) {
        WareHouseDto response = wareHouseService.save(wareHouseDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "warehouse/add";
        }
        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        WareHouseDto response = wareHouseService.findByIdentifier(identifier);
        model.addAttribute("wareHouse", response);
        return "warehouse/warehouse";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute WareHouseDto wareHouseDto) {
        WareHouseDto response = wareHouseService.update(wareHouseDto);
        if (!response.isSuccess()) {
            model.addAttribute("wareHouse", response);
            model.addAttribute("message", response.getMessage());
            return "warehouse/warehouse";
        }
        return REDIRECT_WAREHOUSE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        wareHouseService.delete(identifier);
        return REDIRECT_WAREHOUSE_LIST;
    }

    @PostMapping("/toggle-status")
    @ResponseBody
    public void toggle(Model model, @RequestParam String identifier) {
        wareHouseService.toggleStatus(identifier);
    }
}