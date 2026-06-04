package com.ust.pos.models;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.models.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/model")
public class ModelController {

    public static final String REDIRECT_LIST = "redirect:/model/list";
    public static final String MODEL = "model";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String SUCCESS_MESSAGE = "successMessage";

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
    public String addPost(@ModelAttribute ModelDto modelDto,
                          RedirectAttributes redirectAttributes) {

        ModelDto response = modelService.save(modelDto);

        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, response.getMessage());
            return "redirect:/model/add";
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Model added successfully!");
        return REDIRECT_LIST;
    }

    @GetMapping("/get")
    public String get(Model model,
                      @RequestParam String identifier,
                      RedirectAttributes redirectAttributes) {

        try {
            model.addAttribute(MODEL, modelService.findByIdentifier(identifier));
            return "model/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Model not found!");
            return REDIRECT_LIST;
        }
    }

    @PostMapping("/update")
    public String update(@ModelAttribute ModelDto modelDto,
                         RedirectAttributes redirectAttributes) {

        ModelDto response = modelService.update(modelDto);

        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, response.getMessage());
            return "redirect:/model/get?identifier=" + modelDto.getIdentifier();
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Model updated successfully!");
        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier,
                         RedirectAttributes redirectAttributes) {

        try {
            modelService.delete(identifier);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Model deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Failed to delete model!");
        }

        return REDIRECT_LIST;
    }

    @PostMapping("/toggle")
    public String toggleModel(@RequestParam String identifier,
                              RedirectAttributes redirectAttributes) {

        try {
            modelService.toggleStatus(identifier);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Status updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Failed to update status!");
        }

        return REDIRECT_LIST;
    }
}
