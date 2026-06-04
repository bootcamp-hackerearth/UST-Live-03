package com.ust.pos.unit;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.unit.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/unit")
public class UnitController extends BaseController {

    public static final String REDIRECT_UNIT_LIST = "redirect:/unit/list";

    @Autowired
    private UnitService unitService;

    @GetMapping("/list")
    public String home(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("units", unitService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "unit/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute UnitDto unitDto) {
        return "unit/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute UnitDto unitDto) {
        UnitDto response = unitService.save(unitDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "unit/add";
        }
        return REDIRECT_UNIT_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        UnitDto response = unitService.findByIdentifier(identifier);
        model.addAttribute("unit", response);
        return "unit/unit";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute UnitDto unitDto) {
        UnitDto response = unitService.update(unitDto);
        if (!response.isSuccess()) {
            model.addAttribute("unit", response);
            model.addAttribute("message", response.getMessage());
            return "unit/unit";
        }
        return REDIRECT_UNIT_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        unitService.delete(identifier);
        return REDIRECT_UNIT_LIST;
    }

    @PostMapping("/toggle-status")
    @ResponseBody
    public void toggle(Model model, @RequestParam String identifier) {
        unitService.toggleStatus(identifier);
    }
}
