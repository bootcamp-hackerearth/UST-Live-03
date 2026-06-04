package com.ust.pos.model;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.model.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/model")
public class ModelController {

    public static final String REDIRECT_MODEL_LIST = "redirect:/model/list";
    public static final String MODEL = "model";

    @Autowired
    private ModelService modelService;

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(MODEL, new ModelDto());
        return "model/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute(MODEL) ModelDto modelDto) {
        ModelDto response = modelService.save(modelDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(MODEL, modelDto);
            return "model/add";
        }
        return REDIRECT_MODEL_LIST;
    }

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("models", modelService.findAll(pageable));
        return "model/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ModelDto response = modelService.findByIdentifier(identifier);
        model.addAttribute(MODEL, response);
        return "model/model";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute(MODEL) ModelDto modelDto) {
        ModelDto response = modelService.update(modelDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "model/model";
        }
        return REDIRECT_MODEL_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        modelService.delete(identifier);
        return REDIRECT_MODEL_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        modelService.toggleStatus(identifier);
        return REDIRECT_MODEL_LIST;
    }

}