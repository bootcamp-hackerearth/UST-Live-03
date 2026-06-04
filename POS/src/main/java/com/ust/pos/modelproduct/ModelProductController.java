package com.ust.pos.modelproduct;

import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.modelproduct.service.ModelProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/model")
public class ModelProductController {

    public static final String REDIRECT_MODEL_LIST = "redirect:/model/list";
    public static final String MODELS = "models";
    @Autowired
    private ModelProductService modelProductService;

    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute(MODELS, modelProductService.findAll());
        return "model/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ModelProductDto modelProductDto) {
        model.addAttribute("modelProducts", modelProductService.findAll());
        return "model/add";
    }

    @PostMapping("/add")
    public String addModel(Model model, @ModelAttribute ModelProductDto modelProductDto) {
        ModelProductDto modelProductDto1 = modelProductService.save(modelProductDto);
        if (!modelProductDto.isSuccess()) {
            model.addAttribute("message", modelProductDto1.getMessage());
            model.addAttribute("modelProducts", modelProductService.findAll());
            return "model/add";
        }
        return REDIRECT_MODEL_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ModelProductDto modelProductDto = modelProductService.findByIdentifier(identifier);
        model.addAttribute("modelProduct", modelProductDto);
        model.addAttribute(MODELS, modelProductService.findAll());
        return "model/model";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ModelProductDto modelProductDto) {
        ModelProductDto modelProductDto1 = modelProductService.update(modelProductDto);
        if (!modelProductDto1.isSuccess()) {
            model.addAttribute("message", modelProductDto1.getMessage());
            model.addAttribute(MODELS, modelProductService.findAll());
            return "model/model";
        }
        return REDIRECT_MODEL_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        modelProductService.delete(identifier);
        return REDIRECT_MODEL_LIST;
    }

    @PostMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier) {
        modelProductService.toggleStatus(identifier);
        return REDIRECT_MODEL_LIST;
    }
}
