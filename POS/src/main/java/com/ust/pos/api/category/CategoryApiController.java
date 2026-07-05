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
public class CategoryApiController extends BaseController {
    private final CategoryService categoryService;

    public CategoryApiController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public WsDto<CategoryDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Category> example = buildGlobalSearchSpec(Category.class, paginationDto.getKeyword());
            if (example != null) {
                return categoryService.findAll(example, pageable);
            }
        }
        return categoryService.findAll(pageable);
    }

    @GetMapping("/getAllActive")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public List<CategoryDto> getAllActive() {
        return categoryService.findAllActive();
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public CategoryDto add(@RequestBody CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public CategoryDto getByIdentifier(@RequestParam String identifier) {
        return categoryService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public CategoryDto update(@RequestBody CategoryDto categoryDto) {
        return categoryService.update(categoryDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public boolean delete(@RequestParam String identifier) {
        try {
            categoryService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public CategoryDto toggleStatus(@RequestParam String identifier) {
        return categoryService.toggleStatus(identifier);
    }
}