package com.ust.pos.category;

import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/category")
public class CategoryController extends BaseController {

    public static final String CATEGORY_CATEGORY = "category/category";
    public static final String REDIRECT_CATEGORY_LIST1 = "redirect:/category/list";
    private static final String CATEGORY_LIST = "category/list";
    private static final String CATEGORY_ADD = "category/add";

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String list(Model model, @ModelAttribute PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),paginationDto.getSortField());
        model.addAttribute("categories", categoryService.findAll(pageable));
        return CATEGORY_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute CategoryDto categoryDto) {
        model.addAttribute("categorys", categoryService.findAll(null));
        return CATEGORY_ADD;
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute CategoryDto categoryDto) {
        categoryService.save(categoryDto);
        return REDIRECT_CATEGORY_LIST1;
    }

    @GetMapping("/get")
    public String update(@RequestParam String identifier, Model model) {
        CategoryDto response = categoryService.findByIdentifier(identifier);
        model.addAttribute("category", response);
        model.addAttribute("categories", categoryService.findAll(null));
        return CATEGORY_CATEGORY;
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute CategoryDto categoryDto) {
        CategoryDto response = categoryService.update(categoryDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_CATEGORY_LIST1;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        categoryService.delete(identifier);
        return REDIRECT_CATEGORY_LIST1;
    }

    @PostMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier, boolean status) {
        categoryService.toggleStatus(identifier, status);
        return REDIRECT_CATEGORY_LIST1;
    }
}