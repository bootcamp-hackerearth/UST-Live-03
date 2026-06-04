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
public class CategoryControllerApi extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/category/list";

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/list")
    public List<CategoryDto> list(@RequestBody PaginationDto pagination) {
        Pageable pageable = getPageable(pagination.getPage(), pagination.getSizePerPage(),
                pagination.getSortDirection(), pagination.getSortfield());
        return categoryService.findAll(pageable);
    }

    @PostMapping("/add")
    public CategoryDto addPost(@RequestBody CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
    }

    @GetMapping("/get")
    public CategoryDto updatePage(@RequestParam String identifier) {
        return categoryService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public CategoryDto updatePost(@RequestBody CategoryDto categoryDto) {
        return categoryService.update(categoryDto);
    }

    @PostMapping("/delete")
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