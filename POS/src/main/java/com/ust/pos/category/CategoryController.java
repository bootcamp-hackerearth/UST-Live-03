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

    public static final String REDIRECT_CATEGORY_LIST = "redirect:/category/list";
    public static final String CATEGORY = "category";
    public static final String CATEGORY_LIST = "categoryList";

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {

        model.addAttribute(CATEGORY, categoryService.findAll(pageable));
        return "category/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute CategoryDto categoryDto, Pageable pageable) {

        model.addAttribute(CATEGORY_LIST, categoryService.findAll(pageable));
        return "category/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute CategoryDto categoryDto, Pageable pageable) {

        CategoryDto response = categoryService.save(categoryDto);

        if (!response.isSuccess()) {
            model.addAttribute(CATEGORY_LIST, categoryService.findAll(pageable));
            model.addAttribute("message", response.getMessage());
            return "category/add";
        }
        return REDIRECT_CATEGORY_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier, Pageable pageable) {

        CategoryDto response = categoryService.findByIdentifier(identifier);
        model.addAttribute(CATEGORY_LIST, categoryService.findAll(pageable));
        model.addAttribute(CATEGORY, response);
        return "category/category";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute CategoryDto categoryDto, Pageable pageable) {

        CategoryDto response = categoryService.update(categoryDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(CATEGORY_LIST, categoryService.findAll(pageable));
            model.addAttribute(CATEGORY, categoryDto);
            return "category/category";
        }

        return REDIRECT_CATEGORY_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {

        categoryService.delete(identifier);
        return REDIRECT_CATEGORY_LIST;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public String toggle(@RequestParam String identifier, boolean status) {

        categoryService.changeStatus(identifier, status);
        return "success";
    }
}

