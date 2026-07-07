package com.ust.pos.api.category;

import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.model.Category;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryControllerApi extends BaseController {

    private final CategoryService categoryService;

    @PostMapping("/list")
    public PaginatedResponseDto<CategoryDto> list(@RequestBody PaginationDto paginationDto) {
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
    public CategoryDto addPost(@RequestBody CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
    }

    @GetMapping("/get")
    public CategoryDto get(@RequestParam String identifier) {
        return categoryService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public CategoryDto updatePost(@RequestBody CategoryDto categoryDto) {
        return categoryService.update(categoryDto);
    }

    @DeleteMapping("/delete")
    public CategoryDto delete(@RequestBody CategoryDto categoryDto) {
        try {
            return categoryService.delete(categoryDto.getIdentifier());
        } catch (Exception e) {
            CategoryDto errorDto = new CategoryDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Delete failed: " + e.getMessage());
            return errorDto;
        }
    }

    @PatchMapping("/toggle")
    public boolean changeStatus(@RequestBody CategoryDto categoryDto) {
        try {
            categoryService.changeStatus(categoryDto.getIdentifier(), categoryDto.getStatus());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/active")
    public List<CategoryDto> getActiveCategorys() {
        return categoryService.findAllActive();
    }
}