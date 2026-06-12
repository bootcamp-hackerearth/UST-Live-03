package com.ust.pos.models;


import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.models.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/models")
public class ModelsController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/models/list";

    @Autowired
    private ModelService modelsService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {

        model.addAttribute("models", modelsService.findAll(pageable));
        return "models/list";
    }

    @GetMapping("/add")
    public String add(Model model) {

        model.addAttribute("categories", categoryService.findAllCategoriesWithNoSuper(null));
        model.addAttribute("modelsDto", new ModelsDto());

        return "models/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ModelsDto modelsDto) {

        ModelsDto response = modelsService.save(modelsDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "models/add";
        }

        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {

        ModelsDto response = modelsService.findByIdentifier(identifier);
        model.addAttribute("categories", categoryService.findAllCategoriesWithNoSuper(null));
        model.addAttribute("modelsDto", response);

        return "models/models";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ModelsDto modelsDto) {

        ModelsDto response = modelsService.update(modelsDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "models/models";
        }

        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {

        modelsService.delete(identifier);
        return REDIRECT_ROLE_LIST;
    }


    @GetMapping("/toggle")
    @ResponseBody
    public String toggle(@RequestParam String identifier) {

        modelsService.toggleStatus(identifier);
        return REDIRECT_ROLE_LIST;
    }
}


