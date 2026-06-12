package com.ust.pos.api.category;

import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryApiController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/list")
    public WsDto<CategoryDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return categoryService.findAll(pageable);
    }

    @PostMapping("/add")
    public CategoryDto add(@RequestBody CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
    }

    @GetMapping("/get")
    public CategoryDto get(@RequestParam String identifier) {
        return categoryService.findByIdentifier(identifier);
    }

    @GetMapping("/childCategories")
    public List<CategoryDto> findChildCategories() {
        return categoryService.findChildCategories();
    }

    @PostMapping("/update")
    public CategoryDto update(@RequestBody CategoryDto categoryDto) {
        return categoryService.update(categoryDto);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody CategoryDto categoryDto) {
        try {
            categoryService.delete(categoryDto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}