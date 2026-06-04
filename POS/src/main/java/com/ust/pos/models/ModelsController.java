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
    public static final String MODELS = "models";
    @Autowired
    private ModelsService modelsService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(MODELS, modelsService.findAll(pageable));
        return "models/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ModelsDto modelsDto, Pageable pageable) {
        model.addAttribute(MODELS, modelsService.findAll(pageable));
        return "models/add";
    }

    @PostMapping("/add")
    public String addModel(Model model, @ModelAttribute ModelsDto modelsDto) {
        ModelsDto modelsDto1 = modelsService.save(modelsDto);
        if (!modelsDto.isSuccess()) {
            model.addAttribute("message", modelsDto1.getMessage());
            return "models/add";
        }
        return REDIRECT_MODELS_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ModelsDto modelsDto = modelsService.findByIdentifier(identifier);
        model.addAttribute(MODELS, modelsDto);
        return "models/models";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ModelsDto modelsDto, Pageable pageable) {
        ModelsDto modelsDto1 = modelsService.update(modelsDto);
        if (!modelsDto1.isSuccess()) {
            model.addAttribute("message", modelsDto1.getMessage());
            model.addAttribute(MODELS, modelsService.findAll(pageable));
            return "models/update";
        }
        return REDIRECT_MODELS_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        modelsService.delete(identifier);
        return REDIRECT_MODELS_LIST;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public String toggle(@RequestParam String identifier, boolean status) {
        modelsService.updateStatus(identifier, status);
        return "success";
    }
}