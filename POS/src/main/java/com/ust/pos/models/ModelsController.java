package com.ust.pos.models;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.models.service.ModelsService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/models")
public class ModelsController {

    public static final String MESSAGE = "message";
    private static final String REDIRECT_LIST = "redirect:/models/list";
    @Autowired
    private ModelsService modelsService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("models", modelsService.findAll(pageable));
        return "models/list";
    }

    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("modelsDto", new ModelsDto());
        return "models/add";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ModelsDto modelsDto, Model model) {
        ModelsDto response = modelsService.save(modelsDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            return "models/add";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/edit/{identifier}")
    public String edit(@PathVariable String identifier, Model model) {
        ModelsDto dto = modelsService.findByIdentifier(identifier);
        if (dto == null) {
            model.addAttribute(MESSAGE, "Model not found");
            return REDIRECT_LIST;
        }
        model.addAttribute("model", dto);
        return "models/models";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute ModelsDto modelsDto, Model model) {
        ModelsDto response = modelsService.update(modelsDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute("model", modelsDto);
            return "models/models";
        }
        return REDIRECT_LIST;
    }

    @Transactional
    @GetMapping("/delete/{identifier}")
    public String delete(@PathVariable String identifier) {
        modelsService.delete(identifier);
        return REDIRECT_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        modelsService.toggleStatus(identifier);
        return REDIRECT_LIST;
    }
}