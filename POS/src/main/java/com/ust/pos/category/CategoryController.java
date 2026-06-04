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
    private CategoryService categoryService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
        return "category/list";
    }

    @GetMapping("/add")
    public String add(Model model, Pageable pageable, @ModelAttribute CategoryDto categoryDto) {
        model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
        return "category/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, Pageable pageable, @ModelAttribute CategoryDto categoryDto) {
        CategoryDto response = categoryService.save(categoryDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
            return "category/add";
        }
        return REDIRECT_CATEGORY_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, Pageable pageable, @RequestParam String identifier, @ModelAttribute CategoryDto categoryDto) {
        CategoryDto response = categoryService.findByIdentifier(identifier);
        model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
        model.addAttribute("category", response);
        return "category/category";
    }

    @PostMapping("/update")
    public String updatePost(Model model, Pageable pageable, @ModelAttribute CategoryDto categoryDto) {
        CategoryDto response = categoryService.update(categoryDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("category", categoryService.findAll(pageable));
        }
        return REDIRECT_CATEGORY_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        categoryService.delete(identifier);
        return REDIRECT_CATEGORY_LIST;
    }
}
