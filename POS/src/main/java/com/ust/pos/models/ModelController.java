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

    public static final String REDIRECT_ROLE_LIST = "redirect:/model/list";

    @Autowired
    private ModelService modelService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("models", modelService.findAll(pageable));
        return "model/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ModelDto userDto) {
        return "model/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ModelDto userDto) {
        ModelDto response = modelService.save(userDto);
        if (!response.isSuccess()) {
            model.addAttribute("error", response.getMessage());
            return "model/add";
        }
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ModelDto response = modelService.findByIdentifier(identifier);
        model.addAttribute("model", response);
        return "model/model";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ModelDto userDto) {
        ModelDto response = modelService.update(userDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("model", userDto);
            return "model/model";
        }
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        modelService.delete(identifier);
        return REDIRECT_ROLE_LIST;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public String toggle(@RequestParam String identifier, boolean status) {
        modelService.changeStatus(identifier, status);
        return "success";
    }
}
