package com.ust.pos.unit;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.unit.service.UnitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/unit")
public class UnitController {

    public static final String REDIRECT_UNIT_LIST = "redirect:/unit/list";

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute("units", unitService.findAll());
        return "unit/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute UnitDto unitDto) {
        model.addAttribute("units", unitService.findAll());
        return "unit/add";
    }

    @PostMapping("/add")
    public String addModel(Model model, @ModelAttribute UnitDto unitDto) {
        UnitDto unitDto1 = unitService.save(unitDto);
        if (!unitDto.isSuccess()) {
            model.addAttribute("message", unitDto1.getMessage());
            return "unit/add";
        }
        return REDIRECT_UNIT_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        UnitDto unitDto = unitService.findByIdentifier(identifier);
        model.addAttribute("unit", unitDto);
        return "unit/unit";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute UnitDto unitDto) {
        UnitDto unitDto1 = unitService.update(unitDto);
        if (!unitDto1.isSuccess()) {
            model.addAttribute("message", unitDto1.getMessage());
            return "unit/update";
        }
        return REDIRECT_UNIT_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        unitService.delete(identifier);
        return REDIRECT_UNIT_LIST;
    }

    @PostMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier) {
        unitService.toggleStatus(identifier);
        return REDIRECT_UNIT_LIST;
    }
}