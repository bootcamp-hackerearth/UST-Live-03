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

    public static final String REDIRECT_ROLE_LIST = "redirect:/category/list";

    private final CategoryService categoryService;

    public CategoryControllerApi(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
    public WsDto<CategoryDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortfield());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Category> example = buildGlobalSearchSpec(Category.class, paginationDto.getKeyword());
            if (example != null) {
                return categoryService.findAll(example, pageable);
            }
        }
        return categoryService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
    public CategoryDto addPost(@RequestBody CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
    public CategoryDto updatePage(@RequestParam String identifier) {
        return categoryService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
    public CategoryDto updatePost(@RequestBody CategoryDto categoryDto) {
        return categoryService.update(categoryDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
    public CategoryDto delete(@RequestBody CategoryDto categoryDto) {
        CategoryDto response = new CategoryDto();
        try {
            categoryService.delete(categoryDto.getIdentifier());
            response.setSuccess(true);
            response.setMessage("Category deleted successfully");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Delete failed");
        }
        return response;
    }

    @GetMapping("/findChildCategories")
    public List<CategoryDto> findChildCategories() {
        return categoryService.findChildCategories();
    }

    @GetMapping("/super")
    public List<CategoryDto> findSuperCategories() {
        return categoryService.findSuperCategories();
    }

    @GetMapping("/leaf")
    public List<CategoryDto> findLeafCategories() {
        return categoryService.findLeafCategories();
    }
}