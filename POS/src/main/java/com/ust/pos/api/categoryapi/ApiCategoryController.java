package com.ust.pos.api.categoryapi;

import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.Customer;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class ApiCategoryController extends BaseController {

    public ApiCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    private final CategoryService categoryService;

    @PostMapping("/list")
    public WsDto<CategoryDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Category> example = buildGlobalSearchSpec(Category.class, paginationDto.getKeyword());
            if (example != null) {
                return categoryService.findAll(example, pageable, paginationDto.getKeyword());
            }
        }
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

    @PutMapping("/update")
    public CategoryDto updatePost(@RequestBody CategoryDto categoryDto) {
        return categoryService.update(categoryDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            categoryService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/getBySuperCategoryNotNull")
    public List<CategoryDto> superCategoryNotNull() {
        return categoryService.findBySuperCategoryNotNull();
    }

    @GetMapping("/getAllActiveCategories")
    public List<CategoryDto> getAllActiveCategories() {
        return categoryService.findAllActiveCategories();
    }

    @PostMapping("/toggle-status")
    public CategoryDto toggle(@RequestParam String identifier) {
        return categoryService.toggleStatus(identifier);
    }
}