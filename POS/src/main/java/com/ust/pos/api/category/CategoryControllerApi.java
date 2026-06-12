package com.ust.pos.api.category;
import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/category")
public class CategoryControllerApi extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/list")
    public PageDto<CategoryDto> category(@RequestBody PaginationDto paginationDto) {
        Pageable pageable=getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),paginationDto.getSortDirection(),paginationDto.getSortField());
        return categoryService.findAll(pageable);
    }

    @GetMapping("/identifier")
    public CategoryDto getCategoryByIdentifier(@RequestParam String identifier) {
        return categoryService.findByIdentifier(identifier);
    }


    @GetMapping("/subcategory")
    public List<CategoryDto> getCategoriesBySubCategory() {
        return categoryService.findBySubCategory();
    }


    @PostMapping("/add")
    public CategoryDto addPost( @RequestBody CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
    }


    @PostMapping("/update")
    public CategoryDto updatePost(@RequestBody CategoryDto categoryDto) {
        return categoryService.update(categoryDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            categoryService.delete(identifier);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }
    @GetMapping("/toggleStatus")
    public void toggleStatus(@RequestParam String identifier) {
        categoryService.toggleStatus(identifier);
    }
    @GetMapping("/findByStatus")
    public List<CategoryDto> findByStatus() {
        return categoryService.findActiveCategories();
    }
}
