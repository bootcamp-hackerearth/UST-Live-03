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

    public static final String REDIRECT_MODELS_LIST = "redirect:/models/list";

    @Autowired
    private ModelsService modelsService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("modelss", modelsService.findAll(pageable));
        return "models/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute ModelsDto modelsDto) {
        return "models/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ModelsDto modelsDto) {
        ModelsDto response = modelsService.save(modelsDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "models/add";
        }
        return REDIRECT_MODELS_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ModelsDto response = modelsService.findByIdentifier(identifier);
        model.addAttribute("models", response);
        return "models/models";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ModelsDto modelsDto) {
        ModelsDto response = modelsService.update(modelsDto);
        if (!response.isSuccess()) {
            model.addAttribute("models", response);
            model.addAttribute("message", response.getMessage());
            return "models/models";
        }
        return REDIRECT_MODELS_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        modelsService.delete(identifier);
        return REDIRECT_MODELS_LIST;
    }

    @PostMapping("/toggle-status")
    @ResponseBody
    public void toggle(@RequestParam String identifier) {
        modelsService.toggleStatus(identifier);
    }

}