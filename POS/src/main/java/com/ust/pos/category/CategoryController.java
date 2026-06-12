package com.ust.pos.category;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/category")
public class CategoryController {
    public static final String CATEGORIES = "categories";
    public static final String REDIRECT_CATEGORY_LIST = "redirect:/category/list";
    @Autowired
    CategoryService categoryService;

    @GetMapping("/list")
    public String listCategories(Model model, Pageable pageable) {
        model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
        return "category/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, Pageable pageable) {
        model.addAttribute("categoryDto", new CategoryDto());
        model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
        return "category/add";
    }

    @PostMapping("/add")
    public String saveCategory(@ModelAttribute CategoryDto categoryDto) {
        categoryService.save(categoryDto);
        return REDIRECT_CATEGORY_LIST;
    }


    @GetMapping("/save")
    public String showEditPage(@RequestParam Long id, Model model, Pageable pageable) {

        model.addAttribute("categoryDto", categoryService.findById(id));
        model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
        return "category/category";
    }

    @PostMapping("/save")
    public String saveEditedCategory(@ModelAttribute CategoryDto categoryDto) {

        categoryService.update(categoryDto);
        return REDIRECT_CATEGORY_LIST;
    }

    @GetMapping("/delete")
    public String deleteCategory(@RequestParam Long id) {
        categoryService.deleteById(id);
        return REDIRECT_CATEGORY_LIST;
    }
}