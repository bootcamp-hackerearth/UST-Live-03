package com.ust.pos.api.category;

import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class ApiCategoryController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/add")
    public CategoryDto add(@RequestBody CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
    }

    @PostMapping("/list")
    public List<CategoryDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return categoryService.findAll(pageable);
    }

    @GetMapping("/get")
    public CategoryDto get(@RequestParam("identifier") String identifier) {
        return categoryService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public CategoryDto update(@RequestBody CategoryDto categoryDto) {
        return categoryService.update(categoryDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam("identifier") String identifier) {
        try {
            categoryService.deleteByIdentifier(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/findChildCategories")
    public List<CategoryDto> findChildCategories() {
        return categoryService.findChildCategories();
    }
}
