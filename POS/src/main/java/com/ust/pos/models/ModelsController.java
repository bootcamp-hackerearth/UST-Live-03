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

    public static final String MODELS = "models";
    public static final String REDIRECT_MODELS_LIST = "redirect:/models/list";
    @Autowired
    private ModelsService modelsService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(MODELS, modelsService.findAll(pageable));
        return "models/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ModelsDto modelsDto) {
        return "models/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, Pageable pageable, @ModelAttribute ModelsDto modelsDto) {
        ModelsDto response = modelsService.save(modelsDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(MODELS, modelsService.findAll(pageable));
            return "models/add";
        }
        return REDIRECT_MODELS_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, Pageable pageable, @RequestParam String identifier, @ModelAttribute ModelsDto modelsDto) {
        ModelsDto response = modelsService.findByIdentifier(identifier);
        model.addAttribute(MODELS, modelsService.findAll(pageable));
        model.addAttribute(MODELS, response);
        return "models/models";
    }

    @PostMapping("/update")
    public String updatePost(Model model, Pageable pageable, @ModelAttribute ModelsDto modelsDto) {
        ModelsDto response = modelsService.update(modelsDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(MODELS, modelsService.findAll(pageable));
        }
        return REDIRECT_MODELS_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        modelsService.delete(identifier);
        return REDIRECT_MODELS_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        modelsService.toggleStatus(identifier);
        return REDIRECT_MODELS_LIST;
    }
}
