package com.ust.pos.unit;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.unit.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/unit")
public class UnitController {

    public static final String UNITS = "units";
    public static final String REDIRECT_UNIT_LIST = "redirect:/unit/list";
    @Autowired
    private UnitService unitService;

    @GetMapping("/list")
    public String home(Model unit) {
        unit.addAttribute(UNITS, unitService.findAll());
        return "unit/list";
    }

    @GetMapping("/add")

    public String add(Model unit, @ModelAttribute UnitDto unitDto) {
        unit.addAttribute(UNITS, unitService.findAll());
        return "unit/add";
    }

    @PostMapping("/add")
    public String addModel(Model unit, @ModelAttribute UnitDto unitDto) {
        UnitDto unitDto1 = unitService.save(unitDto);
        if (!unitDto.isSuccess()) {
            unit.addAttribute("message", unitDto1.getMessage());
            unit.addAttribute(UNITS, unitService.findAll());
            return "unit/add";
        }
        return REDIRECT_UNIT_LIST;
    }

    @GetMapping("/get")
    public String update(Model unit, @RequestParam String identifier) {
        UnitDto unitDto = unitService.findByIdentifier(identifier);
        unit.addAttribute("unit", unitDto);
        unit.addAttribute(UNITS, unitService.findAll());
        return "unit/unit";
    }

    @PostMapping("/update")
    public String updatePost(Model unit, @ModelAttribute UnitDto unitDto) {
        UnitDto unitDto1 = unitService.update(unitDto);
        if (!unitDto1.isSuccess()) {
            unit.addAttribute("message", unitDto1.getMessage());
            unit.addAttribute(UNITS, unitService.findAll());
            return "unit/update";
        }
        return REDIRECT_UNIT_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model unit, @RequestParam String identifier) {
        unitService.delete(identifier);
        return REDIRECT_UNIT_LIST;
    }
}


