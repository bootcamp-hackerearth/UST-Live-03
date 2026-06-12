package com.ust.pos.unit;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.unit.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/unit")
public class UnitController {
    public static final String REDIRECT_UNIT_LIST = "redirect:/unit/list";
    public static final String UNITS = "units";

    @Autowired
    UnitService unitService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(UNITS, unitService.findAll(pageable));
        return "unit/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute UnitDto unitDto) {
        model.addAttribute(UNITS,unitDto);
        return "unit/add";
    }

    @PostMapping("/add")
    public String addunit(Model model, @ModelAttribute UnitDto unitDto) {
        unitService.save(unitDto);
        model.addAttribute(UNITS,unitDto);
        return REDIRECT_UNIT_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        unitService.delete(identifier);
        return REDIRECT_UNIT_LIST;
    }

    @GetMapping("/get")
    public String update(org.springframework.ui.Model model, @RequestParam String identifier) {
        UnitDto response = unitService.findByIdentifier(identifier);
        model.addAttribute(UNITS, response);
        return "unit/unit";
    }

    @PostMapping("/update")
    public String updatePrice(Model model, @ModelAttribute UnitDto unitDto) {
        UnitDto response = unitService.update(unitDto);
        model.addAttribute(UNITS,response);
        if (!response.isSuccess()) {
            model.addAttribute(UNITS,response);
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_UNIT_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status){
        unitService.changeToggleStatus(identifier,status);
        return REDIRECT_UNIT_LIST;
    }
}
