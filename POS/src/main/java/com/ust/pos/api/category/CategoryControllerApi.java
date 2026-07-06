package com.ust.pos.api.category;

import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryControllerApi extends BaseController {

    private final CategoryService categoryService;

    public CategoryControllerApi(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin')")
    public WsDto<CategoryDto> listCategories(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Category> example = buildGlobalSearchSpec(Category.class, paginationDto.getKeyword());
            if (example != null) {
                return categoryService.findAll(example, pageable);
            }
        }
        return categoryService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin')")
    public CategoryDto saveCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.save(categoryDto);

    }

    @GetMapping("/update")
    public CategoryDto showEditPage(@RequestParam Long id) {

        return categoryService.findById(id);

    }

    @PutMapping("/update")
    public CategoryDto saveEditedCategory(@RequestBody CategoryDto categoryDto) {

        return categoryService.update(categoryDto);
    }

    @DeleteMapping("/delete")
    public boolean deleteCategory(@RequestParam String identifier) {
        try {
            categoryService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/findActiveSubCategories")
    public List<CategoryDto> findsubcategories() {
        return categoryService.findSubCategories();
    }

    @PostMapping("/changeStatus")
    public CategoryDto toggle(@RequestBody CategoryDto categoryDto) {
        return categoryService.changeCategoryStatus(categoryDto.getIdentifier(), categoryDto.isStatus());
    }
}