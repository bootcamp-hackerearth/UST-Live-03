package com.ust.pos.models;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.models.service.ModelsService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/models")
public class ModelsController {
    public static final String REDIRECT_MODELS_LIST = "redirect:/models/list";

    private final ModelsService modelsService;

    public ModelsController(ModelsService modelsService) {
        this.modelsService = modelsService;
    }

    @GetMapping("/list")
    public String listmodels(Model model, Pageable pageable) {
        model.addAttribute("Models", modelsService.findAll(pageable));
        return "models/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("modelsDto", new ModelsDto());
        return "models/add";
    }

    @PostMapping("/add")
    public String saveModels(@ModelAttribute ModelsDto modelsDto) {
        modelsService.save(modelsDto);
        return REDIRECT_MODELS_LIST;
    }


    @GetMapping("/save")
    public String showEditPage(@RequestParam Long id, Model model, Pageable pageable) {

        model.addAttribute("modelsDto", modelsService.findById(id));

        model.addAttribute("categories", modelsService.findAll(pageable));

        return "models/models";
    }

    @PostMapping("/save")
    public String saveEditedModels(@ModelAttribute ModelsDto modelsDto) {

        modelsService.update(modelsDto);

        return REDIRECT_MODELS_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status) {
        modelsService.changeModelsStatus(identifier, status);
        return REDIRECT_MODELS_LIST;
    }

    @GetMapping("/delete")
    public String deleteModels(@RequestParam String identifier) {
        modelsService.delete(identifier);
        return REDIRECT_MODELS_LIST;
    }
}