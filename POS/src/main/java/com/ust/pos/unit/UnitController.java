package com.ust.pos.unit;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.unit.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/unit")
public class UnitController {

    public static final String REDIRECT_LIST = "redirect:/unit/list";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String SUCCESS_MESSAGE = "successMessage";

    @Autowired
    private UnitService unitService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("units", unitService.findAll(pageable));
        return "unit/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("unit", new UnitDto());
        return "unit/add";
    }

    @PostMapping("/add")
    public String addPost(Model model,
                          @ModelAttribute UnitDto unitDto,
                          RedirectAttributes redirectAttributes) {

        UnitDto response = unitService.save(unitDto);

        if (!response.isSuccess()) {
            model.addAttribute("unit", unitDto);
            model.addAttribute("message", response.getMessage());
            return "unit/add";
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Unit added successfully!");
        return REDIRECT_LIST;
    }

    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        model.addAttribute("unit", unitService.findByIdentifier(identifier));
        return "unit/edit";
    }

    @PostMapping("/update")
    public String update(Model model,
                         @ModelAttribute UnitDto unitDto,
                         RedirectAttributes redirectAttributes) {

        UnitDto response = unitService.update(unitDto);

        if (!response.isSuccess()) {
            model.addAttribute("unit", unitDto);
            model.addAttribute("message", response.getMessage());
            return "unit/edit";
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Unit updated successfully!");
        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier,
                         RedirectAttributes redirectAttributes) {

        try {
            unitService.delete(identifier);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Unit deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Failed to delete unit!");
        }

        return REDIRECT_LIST;
    }
}