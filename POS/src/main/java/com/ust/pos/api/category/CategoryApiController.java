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
public class CategoryApiController extends BaseController {

    private final CategoryService categoryService;

    public CategoryApiController(CategoryService categoryService) {

        this.categoryService = categoryService;
    }

    @PostMapping("/list")
    public WsDto<CategoryDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getSearch())) {
            Specification<Category> example = buildGlobalSearchSpec(Category.class, paginationDto.getSearch());
            if (example != null) {
                return categoryService.findAll(example, pageable);
            }
        }
        return categoryService.findAll(pageable);
    }

    @GetMapping("/getCategoriesWithoutParent")
    public List<CategoryDto> listAllCategoryWithNoSuper() {

        return categoryService.findAllCategoriesWithNoSuper();
    }

    @PostMapping("/add")
    public CategoryDto addPost(@RequestBody CategoryDto categoryDto) {

        return categoryService.save(categoryDto);
    }

    @GetMapping("/{identifier}")
    public CategoryDto update(@PathVariable String identifier) {

        return categoryService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public CategoryDto updatePost(@RequestBody CategoryDto categoryDto) {

        return categoryService.update(categoryDto);
    }

    @PatchMapping("/toggle")
    public boolean toggleStatus(@RequestBody String identifier) {

        try {
            categoryService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/getactive")
    public List<CategoryDto> getActiveCategory() {

        return categoryService.findActiveCategory();
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody CategoryDto categoryDto) {

        String identifier = categoryDto.getIdentifier();

        try {
            categoryService.delete(identifier);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}


