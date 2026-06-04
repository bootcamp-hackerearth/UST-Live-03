package com.ust.pos.models;

import com.ust.pos.models.service.ModelService;
import com.ust.pos.dto.ModelDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/model")
public class ModelController {

    public static final String REDIRECT_MODEL_LIST = "redirect:/model/list";
    public static final String MODELS = "models";

    @Autowired
    ModelService modelService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(MODELS, modelService.findAll(pageable));
        return "model/list";
    }
    
    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ModelDto modelDto,Pageable pageable) {
        model.addAttribute(MODELS,modelDto);
        model.addAttribute(MODELS,modelService.findAll(pageable));
        return "model/add";
    }

    @PostMapping("/add")
    public String addModel(Model model, @ModelAttribute ModelDto modelDto) {
        ModelDto response = modelService.save(modelDto);

        if (!response.isSuccess()) {
            model.addAttribute("modelDto", response);
            model.addAttribute("message", response.getMessage());
            return "model/add";
        }

        return REDIRECT_MODEL_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        modelService.delete(identifier);
        return REDIRECT_MODEL_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier,Pageable pageable) {
        ModelDto response = modelService.findByIdentifier(identifier);
        model.addAttribute(MODELS,modelService.findAll(pageable));
        model.addAttribute(MODELS, response);
        return "model/model";
    }

    @PostMapping("/update")
    public String updatePrice(Model model, @ModelAttribute ModelDto modelDto,Pageable pageable) {
        ModelDto response = modelService.update(modelDto);
        model.addAttribute(MODELS,response);
        model.addAttribute(MODELS,modelService.findAll(pageable));
        if (!response.isSuccess()) {
            model.addAttribute(MODELS,response);
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_MODEL_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status){
        modelService.changeToggleStatus(identifier,status);
        return REDIRECT_MODEL_LIST;
    }
}
