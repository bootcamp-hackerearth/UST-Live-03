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
    @Autowired
    UnitService unitService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("units", unitService.findAll(pageable));
        return "unit/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("unitDto", new UnitDto());
        return "unit/add";
    }

    @PostMapping("/add")
    public String saveUnit(@ModelAttribute UnitDto unitDto) {
        unitService.save(unitDto);
        return REDIRECT_UNIT_LIST;
    }


    @GetMapping("/update")
    public String showEditPage(@RequestParam Long id, Model model, Pageable pageable) {

        model.addAttribute("unitDto", unitService.findById(id));
        model.addAttribute("units", unitService.findAll(pageable));
        return "unit/unit";
    }

    @PostMapping("/update")
    public String saveEditedUnit(@ModelAttribute UnitDto unitDto) {

        unitService.update(unitDto);
        return REDIRECT_UNIT_LIST;
    }


    @GetMapping("/delete")
    public String deleteUnit(@RequestParam Long id) {
        unitService.delete(id);
        return REDIRECT_UNIT_LIST;
    }
}
