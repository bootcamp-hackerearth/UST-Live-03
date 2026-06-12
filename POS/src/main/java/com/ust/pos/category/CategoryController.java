package com.ust.pos.category;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.category.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/category")
public class CategoryController {

    public static final String REDIRECT_CATEGORY_LIST = "redirect:/category/list";

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute("categorys", categoryService.findAll());
        return "category/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute CategoryDto userDto) {
        model.addAttribute("categoryList", categoryService.findAll());
        return "category/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute CategoryDto userDto) {
        CategoryDto response = categoryService.save(userDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "category/add";
        }
        return REDIRECT_CATEGORY_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        CategoryDto categoryDto = categoryService.findByIdentifier(identifier);
        model.addAttribute("categoryDto", categoryDto);
        model.addAttribute("categoryList", categoryService.findAll());
        return "category/category";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute CategoryDto categoryDto) {
        CategoryDto response = categoryService.update(categoryDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("categoryDto", categoryDto);
            return "category/category";
        }
        return REDIRECT_CATEGORY_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        categoryService.delete(identifier);
        return REDIRECT_CATEGORY_LIST;
    }
}