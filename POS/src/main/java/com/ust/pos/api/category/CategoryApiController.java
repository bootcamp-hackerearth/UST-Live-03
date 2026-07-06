package com.ust.pos.api.category;

import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.Category;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryApiController extends BaseController {

    private final CategoryService categoryService;

    public CategoryApiController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public PaginationResponseDto<CategoryDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Category> example = buildGlobalSearchSpec(Category.class, paginationDto.getKeyword());
            if (example != null) {
                return categoryService.findAll(example, pageable);
            }
        }
        return categoryService.findAll(pageable);
    }

    @GetMapping("/listSuperCategory")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public List<CategoryDto> listSuperCategory() {
        return categoryService.findBySuperCategoryIsNotNullAndStatusTrue();
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public CategoryDto addPost(@RequestBody CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
    }

    @PutMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public CategoryDto toggleStatus(@RequestBody CategoryDto categoryDto) {
        return categoryService.updateStatus(categoryDto.getIdentifier(), categoryDto.isStatus());
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public CategoryDto update(@RequestParam String identifier) {
        return categoryService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public CategoryDto updatePost(@RequestBody CategoryDto categoryDto) {
        return categoryService.update(categoryDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public boolean delete(@RequestParam String identifier) {
        try {
            categoryService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
