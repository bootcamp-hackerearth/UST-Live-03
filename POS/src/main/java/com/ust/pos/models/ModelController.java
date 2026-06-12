package com.ust.pos.models;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.models.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/model")
public class ModelController extends BaseController {

    public static final String REDIRECT_MODEL_LIST = "redirect:/model/list";

    @Autowired
    ModelService modelService;

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ModelDto modelDto) {
        return "model/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ModelDto modelDto) {
        ModelDto response = modelService.save(modelDto);
        if (!response.isSuccess()) {
            model.addAttribute("models", modelService.findAll(null));
            model.addAttribute("message", response.getMessage());
            return "model/add";
        }
        return REDIRECT_MODEL_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, @ModelAttribute PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),paginationDto.getSortField());
        model.addAttribute("models", modelService.findAll(pageable));
        return "model/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ModelDto response = modelService.findByIdentifier(identifier);
        model.addAttribute("model", response);
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
        modelService.deleteByIdentifier(identifier);
        return REDIRECT_MODEL_LIST;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public ModelDto toggleStatus(@RequestBody ModelDto modelDto) {
        return modelService.toggleStatus(modelDto.getIdentifier(), modelDto.isStatus());
    }
}