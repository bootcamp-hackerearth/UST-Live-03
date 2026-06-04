package com.ust.pos.models;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.models.service.ModelsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/models")
public class ModelsController {

    public static final String REDIRECT_LIST = "redirect:/models/list";
    public static final String MODELS = "models";

    @Autowired
    private ModelsService modelsService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute(MODELS, modelsService.findAll(pageable));
        return "models/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(MODELS, new ModelsDto());
        return "models/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ModelsDto modelsDto) {
        ModelsDto response = modelsService.save(modelsDto);
        if (!response.isSuccess()) {
            model.addAttribute(MODELS, model);
            model.addAttribute("message", response.getMessage());
            return "models/add";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        model.addAttribute(MODELS, modelsService.findByIdentifier(identifier));
        return "models/models";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute ModelsDto modelsDto) {
        ModelsDto response = modelsService.update(modelsDto);
        if (!response.isSuccess()) {
            model.addAttribute(MODELS, model);
            model.addAttribute("message", response.getMessage());
            return "models/models";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        modelsService.delete(identifier);
        return REDIRECT_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        modelsService.toggleStatus(identifier);
        return REDIRECT_LIST;
    }

}