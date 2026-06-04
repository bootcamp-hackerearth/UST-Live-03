package com.ust.pos.models;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.models.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/model")
public class ModelController extends BaseController {
    public static final String REDIRECT_MODEL_LIST = "redirect:/model/list";
    @Autowired
    private ModelService modelService;

    @GetMapping("/list")
    public String home(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("models", modelService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "model/list";
    }


    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ModelDto modelDto) {
        return "model/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ModelDto modelDto) {
        ModelDto response = modelService.save(modelDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("modelDto", modelDto);// retain form values
            return "model/add";

        }
        return REDIRECT_MODEL_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ModelDto response = modelService.findByIdentifier(identifier);
        model.addAttribute("modelDto", response);
        return "model/model";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ModelDto modelDto) {
        ModelDto response = modelService.update(modelDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_MODEL_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        modelService.delete(identifier);
        return REDIRECT_MODEL_LIST;
    }

    @GetMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier) {
        modelService.toggleStatus(identifier);
        return REDIRECT_MODEL_LIST;
    }
}
