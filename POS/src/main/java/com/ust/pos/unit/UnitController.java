package com.ust.pos.unit;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.unit.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/unit")
public class UnitController extends BaseController {
    public static final String REDIRECT_UNIT_LIST = "redirect:/unit/list";
    private static final String MESSAGE = "message";

    @Autowired
    private UnitService unitService;

    @GetMapping("/add")
    public String add() {
        return "unit/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute UnitDto unitDto) {
        UnitDto response = unitService.save(unitDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            return "redirect:/unit/add";
        }
        return REDIRECT_UNIT_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("units", unitService.findAll(pageable));
        return "unit/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        UnitDto response = unitService.findByIdentifier(identifier);
        model.addAttribute("unit", response);
        return "unit/unit";
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute UnitDto unitDto) {
        UnitDto response = unitService.update(unitDto);
        if (!response.isSuccess()) {
            return REDIRECT_UNIT_LIST;
        }
        return "redirect:/unit/get?identifier=" + unitDto.getIdentifier();
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        unitService.delete(identifier);
        return REDIRECT_UNIT_LIST;
    }
}