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

    private static final String MODEL_LIST = "model/list";
    private static final String MODEL_ADD = "model/add";
    private static final String MODEL_VIEW = "model/model";
    private static final String REDIRECT_MODEL_LIST = "redirect:/model/list";

    @Autowired
    private ModelService modelService;

    @GetMapping("/list")
    public String list(Model model, @ModelAttribute PaginationDto paginationDto) {
        model.addAttribute("models", modelService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return MODEL_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ModelDto modelDto) {
        model.addAttribute("models", modelService.findAll(null));
        return MODEL_ADD;
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute ModelDto modelDto) {
        modelService.save(modelDto);
        return REDIRECT_MODEL_LIST;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public ModelDto toggleStatus(@RequestBody ModelDto dto) {
        return modelService.updateStatus(dto.getIdentifier(), dto.isStatus());
    }

    @GetMapping("/get")
    public String update(@RequestParam String identifier, Model model) {
        model.addAttribute("modelDto", modelService.findByIdentifier(identifier));
        return MODEL_VIEW;
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute ModelDto modelDto) {
        modelService.update(modelDto);
        return REDIRECT_MODEL_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        modelService.delete(identifier);
        return REDIRECT_MODEL_LIST;
    }
}
