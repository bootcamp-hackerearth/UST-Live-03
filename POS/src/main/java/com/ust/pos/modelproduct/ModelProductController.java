package com.ust.pos.modelproduct;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.modelproduct.service.ModelProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/modelproduct")
public class ModelProductController extends BaseController {

    private static final String REDIRECT_LIST = "redirect:/modelproduct/list";

    @Autowired
    private ModelProductService modelProductService;

    @GetMapping("/list")
    public String list(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("modelProducts", modelProductService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "modelproduct/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("modelProductDto", new ModelProductDto());
        return "modelproduct/add";
    }

    @PostMapping("/add")
    public String save(Model model, @ModelAttribute ModelProductDto modelProductDto) {
        ModelProductDto response = modelProductService.save(modelProductDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "modelproduct/add";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        model.addAttribute("modelProductDto", modelProductService.findByIdentifier(identifier));
        return "modelproduct/modelproduct";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute ModelProductDto modelProductDto) {
        ModelProductDto response = modelProductService.update(modelProductDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "modelproduct/modelproduct";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        modelProductService.delete(identifier);
        return REDIRECT_LIST;
    }

    @PostMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier) {
        modelProductService.toggleStatus(identifier);
        return REDIRECT_LIST;
    }
}