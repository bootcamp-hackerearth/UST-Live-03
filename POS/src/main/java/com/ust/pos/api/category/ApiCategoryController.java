package com.ust.pos.api.category;

import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class ApiCategoryController extends BaseController {

    private final CategoryService categoryService;

    @PostMapping("/add")
    public CategoryDto add(@RequestBody CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
    }

    @PostMapping("/list")
    public WsDto<CategoryDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return categoryService.findAll(pageable);
    }

    @GetMapping("/get")
    public CategoryDto get(@RequestParam("identifier") String identifier) {
        return categoryService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public CategoryDto update(@RequestBody CategoryDto categoryDto) {
        return categoryService.update(categoryDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam("identifier") String identifier) {
        try {
            categoryService.delete(identifier);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @GetMapping("/findChildCategories")
    public List<CategoryDto> findChildCategories() {
        return categoryService.findChildCategories();
    }

    @PatchMapping("/toggle-status")
    public CategoryDto toggle(@RequestParam String identifier) {
        return categoryService.toggleStatus(identifier);
    }

    @GetMapping("/findallactive")
    public List<CategoryDto> findAllActive() {
        return categoryService.findAllActive();
    }

}