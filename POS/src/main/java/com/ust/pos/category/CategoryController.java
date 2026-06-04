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
    public static final String CATEGORY_LIST = "category/list";
    public static final String MESSAGE = "message";

    @Autowired
    CategoryService categoryService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
        return CATEGORY_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute CategoryDto categoryDto, Pageable pageable) {
        model.addAttribute("allCategories", categoryService.findAll(pageable));
        return "category/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute CategoryDto categoryDto, Pageable pageable) {
        CategoryDto response = categoryService.save(categoryDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Category Added Successfully");
        }
        model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
        return CATEGORY_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier, Pageable pageable) {
        CategoryDto response = categoryService.findByIdentifier(identifier);
        model.addAttribute("allCategories", categoryService.findAll(pageable));
        model.addAttribute("category", response);
        return "category/category";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute CategoryDto categoryDto, Pageable pageable) {
        CategoryDto response = categoryService.update(categoryDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Updated Successfully");
        }
        model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
        return CATEGORY_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier, Pageable pageable) {
        categoryService.delete(identifier);
        model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
        model.addAttribute(MESSAGE, "Category deleted successfully");
        return CATEGORY_LIST;
    }
}
