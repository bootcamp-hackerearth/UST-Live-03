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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryControllerApi extends BaseController {
    public static final String REDIRECT_CATEGORY_LIST = "redirect:/category/list";

    private final CategoryService categoryService;

    public CategoryControllerApi(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/list")
    public WsDto<CategoryDto> list(@RequestBody PaginationDto paginationDto) {
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

    @PatchMapping("/toggle")
    public String toggle(
            @RequestParam String identifier,
            @RequestParam boolean status
    ) {
        categoryService.updateStatus(identifier, status);
        return "success";
    }

    @PostMapping("/list-active")
    public List<CategoryDto> listActive() {
        return categoryService.findAllActive();
    }
}