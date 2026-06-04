package com.ust.pos.api.category;

import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("categoryApiController")
@RequestMapping("/api/categories")
public class CategoryController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/list")
    public ResponseEntity<List<CategoryDto>> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        List<CategoryDto> categories = categoryService.findAll(pageable);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<CategoryDto> getByIdentifier(@PathVariable String identifier) {
        CategoryDto response = categoryService.findByIdentifier(identifier);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<CategoryDto> save(@RequestBody CategoryDto categoryDto) {
        CategoryDto response = categoryService.save(categoryDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{identifier}")
    public ResponseEntity<CategoryDto> update(@PathVariable String identifier, @RequestBody CategoryDto categoryDto) {
        categoryDto.setIdentifier(identifier);
        CategoryDto response = categoryService.update(categoryDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete/{identifier}")
    public ResponseEntity<Boolean> delete(@PathVariable String identifier) {
        try {
            boolean deleted = categoryService.delete(identifier);
            return ResponseEntity.ok(deleted);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @PostMapping("/toggle/{identifier}")
    public ResponseEntity<CategoryDto> toggleStatus(@PathVariable String identifier) {
        CategoryDto response = categoryService.toggleStatus(identifier);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CategoryDto>> activeCategories() {
        List<CategoryDto> activeCategories = categoryService.findIfTrue();
        return ResponseEntity.ok(activeCategories);
    }

    @GetMapping("/super-categories")
    public ResponseEntity<List<CategoryDto>> superCategories() {
        List<CategoryDto> superCategories = categoryService.findSuperCategories();
        return ResponseEntity.ok(superCategories);
    }
}