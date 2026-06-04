package com.ust.pos.api.models;


import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.models.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
public class ModelsApiController extends BaseController {

    @Autowired
    private ModelService modelsService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/list")
    public List<ModelsDto> list(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        return modelsService.findAll(pageable);
    }

    @PostMapping("/add")
    public ModelsDto addPost(@RequestBody ModelsDto modelsDto) {

        return modelsService.save(modelsDto);
    }

    @GetMapping("/get")
    public ModelsDto update(@RequestParam String identifier) {

        return modelsService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ModelsDto updatePost(Model model, @ModelAttribute ModelsDto modelsDto) {

        return modelsService.update(modelsDto);
    }

    @GetMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {

        try {
            modelsService.delete(identifier);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}


