package com.ust.pos.api.category;

import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class ApiCategoryController extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/category/list";
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/list")
    public List<CategoryDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
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

    @PostMapping("/update")
    public CategoryDto updatePost(@RequestBody CategoryDto categoryDto) {

        return categoryService.update(categoryDto);
    }

    @GetMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            categoryService.deleteByIdentifier(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    public CategoryDto toggle(@RequestParam String identifier) {

        return categoryService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<CategoryDto> findByStatus() {

        return categoryService.findIfTrue();
    }

    @GetMapping("/findByIdentifier")
    public CategoryDto findByIdentifier(@RequestParam String identifier) {

        return categoryService.findByIdentifier(identifier);
    }

}
