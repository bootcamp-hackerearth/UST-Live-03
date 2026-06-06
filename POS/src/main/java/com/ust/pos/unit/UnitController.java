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

    public static final String UNITS = "units";
    public static final String REDIRECT_UNIT_LIST = "redirect:/unit/list";
    @Autowired
    private UnitService unitService;

    @GetMapping("/list")
    public String home(Model unit) {
        PaginationDto paginationDto = new PaginationDto();
        unit.addAttribute(UNITS, unitService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "unit/list";
    }

    @GetMapping("/add")
    public String add(Model unit, @ModelAttribute UnitDto unitDto) {
        PaginationDto paginationDto = new PaginationDto();
        unit.addAttribute(UNITS, unitService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "unit/add";
    }

    @PostMapping("/add")
    public String addModel(Model unit, @ModelAttribute UnitDto unitDto) {
        UnitDto unitDto1 = unitService.save(unitDto);
        PaginationDto paginationDto = new PaginationDto();
        if (!unitDto.isSuccess()) {
            unit.addAttribute("message", unitDto1.getMessage());
            unit.addAttribute(UNITS, unitService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                    paginationDto.getSortDirection(), paginationDto.getSortField())));
            return "unit/add";
        }
        return REDIRECT_UNIT_LIST;
    }

    @GetMapping("/get")
    public String update(Model unit, @RequestParam String identifier) {
        PaginationDto paginationDto = new PaginationDto();
        UnitDto unitDto = unitService.findByIdentifier(identifier);
        unit.addAttribute("unit", unitDto);
        unit.addAttribute(UNITS, unitService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "unit/unit";
    }

    @PostMapping("/update")
    public String updatePost(Model unit, @ModelAttribute UnitDto unitDto) {
        UnitDto unitDto1 = unitService.update(unitDto);
        PaginationDto paginationDto = new PaginationDto();
        if (!unitDto1.isSuccess()) {
            unit.addAttribute("message", unitDto1.getMessage());
            unit.addAttribute(UNITS, unitService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                    paginationDto.getSortDirection(), paginationDto.getSortField())));
            return "unit/update";
        }
        return REDIRECT_UNIT_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model unit, @RequestParam String identifier) {
        unitService.delete(identifier);
        return REDIRECT_UNIT_LIST;
    }

    @PostMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier) {
        unitService.toggleStatus(identifier);
        return REDIRECT_UNIT_LIST;
    }
}