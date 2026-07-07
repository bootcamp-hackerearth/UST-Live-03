package com.ust.pos.api.category;

import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/category")
@PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
public class CategoryApiController extends BaseController {
    private final CategoryService categoryService;

    public CategoryApiController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/list")
    public WsDto<CategoryDto> home(@RequestBody PaginationDto paginationDto) throws Exception
    {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        Example<Category> example = buildSearchProbe(Category.class, paginationDto.getSearch());
        Page<CategoryDto> pageResult = categoryService.findAll(example, pageable);

        WsDto<CategoryDto> response = new WsDto<>();

        response.setDtoList(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPage(pageResult.getTotalPages());
        response.setTotalRecords(pageResult.getTotalElements());

        return response;
    }

    @PostMapping("/add")
    public CategoryDto addPost(@RequestBody CategoryDto categoryDto)
    {
        return categoryService.save(categoryDto);
    }

    @GetMapping("/get")
    public CategoryDto update(@RequestParam String identifier)
    {
        return categoryService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public CategoryDto doupdate(@RequestBody CategoryDto categoryDto)
    {
        return categoryService.update(categoryDto);
    }

    @DeleteMapping("/delete")
    public Boolean delete(@RequestParam String identifier)
    {
        try
        {
            categoryService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
