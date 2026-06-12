package com.ust.pos.models;


import com.ust.pos.dto.ModelDto;
import com.ust.pos.models.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/model")
public class ModelController {

    public static final String REDIRECT_LIST = "redirect:/model/list";
    public static final String MODEL = "model";

    @Autowired
    private ModelService modelService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("models", modelService.findAll(pageable));
        return "model/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(MODEL, new ModelDto());
        return "model/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ModelDto modelDto) {
        ModelDto response = modelService.save(modelDto);

        if (!response.isSuccess()) {
            model.addAttribute(MODEL, modelDto);
            model.addAttribute("message", response.getMessage());
            return "model/add";
        }
        return REDIRECT_LIST;
    }


    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        model.addAttribute(MODEL, modelService.findByIdentifier(identifier));
        return "model/edit";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute ModelDto modelDto) {
        ModelDto response = modelService.update(modelDto);

        if (!response.isSuccess()) {
            model.addAttribute(MODEL, modelDto);
            model.addAttribute("message", response.getMessage());
            return "model/edit";
        }
        return REDIRECT_LIST;
    }


    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        modelService.delete(identifier);
        return REDIRECT_LIST;
    }

    @PostMapping("/toggle")
    public String toggleModel(@RequestParam String identifier) {
        modelService.toggleStatus(identifier);
        return REDIRECT_LIST;
    }
}