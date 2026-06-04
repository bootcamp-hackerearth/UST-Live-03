package com.ust.pos.unit;

import com.ust.pos.category.service.CategoryService;
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

    public static final String REDIRECT_ROLE_LIST = "redirect:/unit/list";

    @Autowired
    private UnitService unitService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {

        model.addAttribute("units", unitService.findAll(pageable));
        return "unit/list";
    }

    @GetMapping("/add")
    public String add(Model model) {

        model.addAttribute("unitDto", new UnitDto());
        return "unit/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute UnitDto unitDto) {

        UnitDto response = unitService.save(unitDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "unit/add";
        }

        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {

        UnitDto response = unitService.findByIdentifier(identifier);
        model.addAttribute("categories", categoryService.findAllCategoriesWithSuper());
        model.addAttribute("unitDto", response);

        return "unit/unit";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute UnitDto unitDto) {

        UnitDto response = unitService.update(unitDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "unit/unit";
        }

        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {

        unitService.delete(identifier);
        return REDIRECT_ROLE_LIST;
    }


    @GetMapping("/toggle")
    @ResponseBody
    public String toggle(@RequestParam String identifier) {

        unitService.toggleStatus(identifier);
        return REDIRECT_ROLE_LIST;
    }
}

