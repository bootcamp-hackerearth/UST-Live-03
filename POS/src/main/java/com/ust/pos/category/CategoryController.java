package com.ust.pos.category;

import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/category")
public class CategoryController extends BaseController {

    public static final String CATEGORIES = "categories";
    public static final String CATEGORIES1 = "categories";
    private static final String CATEGORY_LIST = "category/list";
    private static final String CATEGORY_ADD = "category/add";
    private static final String CATEGORY_VIEW = "category/category";
    private static final String REDIRECT_CATEGORY_LIST = "redirect:/category/list";
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String list(Model model, @ModelAttribute PaginationDto paginationDto) {
        model.addAttribute(CATEGORIES, categoryService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return CATEGORY_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute CategoryDto categoryDto) {
        model.addAttribute(CATEGORIES, categoryService.findAll(null));
        return CATEGORY_ADD;
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute CategoryDto categoryDto) {
        categoryService.save(categoryDto);
        return REDIRECT_CATEGORY_LIST;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public CategoryDto toggleStatus(@RequestBody CategoryDto categoryDto) {
        return categoryService.updateStatus(categoryDto.getIdentifier(), categoryDto.isStatus());
    }

    @GetMapping("/get")
    public String update(@RequestParam String identifier, Model model) {
        CategoryDto categoryDto = categoryService.findByIdentifier(identifier);
        model.addAttribute(CATEGORIES1, categoryService.findAll(null));
        model.addAttribute("categoryDto", categoryDto);
        return CATEGORY_VIEW;
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute CategoryDto categoryDto) {
        categoryService.update(categoryDto);
        return REDIRECT_CATEGORY_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        categoryService.delete(identifier);
        return REDIRECT_CATEGORY_LIST;
    }
}
