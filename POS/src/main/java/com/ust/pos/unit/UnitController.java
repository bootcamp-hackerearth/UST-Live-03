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

    private static final String UNIT_LIST = "unit/list";
    private static final String UNIT_ADD = "unit/add";
    private static final String UNIT_VIEW = "unit/unit";
    private static final String REDIRECT_UNIT_LIST = "redirect:/unit/list";

    @Autowired
    private UnitService unitService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("units", unitService.findAll(null));
        return UNIT_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute UnitDto unitDto) {
        model.addAttribute("units", unitService.findAll(null));
        return UNIT_ADD;
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute UnitDto unitDto) {
        unitService.save(unitDto);
        return REDIRECT_UNIT_LIST;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public UnitDto toggleStatus(@RequestBody UnitDto dto) {
        return unitService.updateStatus(dto.getIdentifier(), dto.isStatus());
    }

    @GetMapping("/get")
    public String update(@RequestParam String identifier, Model model) {
        model.addAttribute("unitDto", unitService.findByIdentifier(identifier));
        return UNIT_VIEW;
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute UnitDto unitDto) {
        unitService.update(unitDto);
        return REDIRECT_UNIT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        unitService.delete(identifier);
        return REDIRECT_UNIT_LIST;
    }
}
