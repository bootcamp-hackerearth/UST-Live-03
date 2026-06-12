package com.ust.pos.api.category;

import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public class CategoryControllerApi extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/list")
    public PaginatedResponseDto<CategoryDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return categoryService.findAll(pageable);
    }

    @PostMapping("/add")
    public CategoryDto addPost(@RequestBody CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
    }

    @GetMapping("/get")
    public CategoryDto update(@RequestParam String identifier) {
        return categoryService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public CategoryDto updatePost(@RequestBody CategoryDto categoryDto) {
        return categoryService.update(categoryDto);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody CategoryDto categoryDto) {
        try {
            categoryService.delete(categoryDto.getIdentifier());
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @PostMapping("/toggle")
    public boolean changeStatus(@RequestBody CategoryDto categoryDto) {
        try {
            categoryService.changeStatus(categoryDto.getIdentifier(), categoryDto.getStatus());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
