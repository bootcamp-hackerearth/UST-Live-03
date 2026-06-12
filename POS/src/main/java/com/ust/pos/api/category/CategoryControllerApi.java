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
public class CategoryControllerApi extends BaseController {
    @Autowired
    CategoryService categoryService;

    @PostMapping("/list")
    public WsDto<CategoryDto> listCategories(@RequestBody PaginationDto paginationDto) {
        Pageable pageable
                = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return categoryService.findAll(pageable);

    }

    @PostMapping("/add")
    public CategoryDto saveCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.save(categoryDto);

    }

    @GetMapping("/update")
    public CategoryDto showEditPage(@RequestParam Long id) {

        return categoryService.findById(id);

    }

    @PostMapping("/update")
    public CategoryDto saveEditedCategory(@RequestBody CategoryDto categoryDto) {

        return categoryService.update(categoryDto);
    }

    @GetMapping("/delete")
    public boolean deleteCategory(@RequestParam Long id) {
        try {
            categoryService.deleteById(id);
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