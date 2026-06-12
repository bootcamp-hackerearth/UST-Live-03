package com.ust.pos.api.category;

import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryApiController extends BaseController {
    @Autowired
    CategoryService categoryService;

    @PostMapping("/list")
    public WsDto<CategoryDto> home(
            @RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortField());

        Page<CategoryDto> pageResult =
                categoryService.findAll(
                        pageable,
                        paginationDto.getSearch());

        WsDto<CategoryDto> response = new WsDto<>();

        response.setContent(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPages(pageResult.getTotalPages());

        return response;
    }
    @GetMapping("/list")
    public List<CategoryDto> home() {
        return categoryService.findAll();
    }

    @PostMapping("/add")
    public CategoryDto add(@RequestBody CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
    }

    @GetMapping("/get")
    public CategoryDto update(@RequestParam String identifier) {
        return categoryService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public CategoryDto get(@RequestBody CategoryDto categoryDto) {
        return categoryService.update(categoryDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            categoryService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}