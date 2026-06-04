package com.ust.pos.category;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/category")
public class CategoryController {

    public static final String REDIRECT_CATEGORY_LIST = "redirect:/category/list";
    public static final String CATEGORIES = "categories";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String ERROR_MESSAGE = "errorMessage";

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
        return "category/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute("category") CategoryDto categoryDto, Pageable pageable) {
        model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
        return "category/add";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute("category") CategoryDto categoryDto,
                          RedirectAttributes redirectAttributes) {
        CategoryDto response = categoryService.save(categoryDto);

        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, response.getMessage());
            return "redirect:/category/add";
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Category added successfully!");
        return REDIRECT_CATEGORY_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier, Pageable pageable,
                         RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("category", categoryService.findByIdentifier(identifier));
            model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
            return "category/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Category not found!");
            return REDIRECT_CATEGORY_LIST;
        }
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute("category") CategoryDto categoryDto,
                             RedirectAttributes redirectAttributes) {

        CategoryDto response = categoryService.update(categoryDto);

        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, response.getMessage());
            return "redirect:/category/get?identifier=" + categoryDto.getIdentifier();
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Category updated successfully!");
        return REDIRECT_CATEGORY_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier,
                         RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteByIdentifier(identifier);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Category deleted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, ex.getMessage());
        }
        return REDIRECT_CATEGORY_LIST;
    }

    @PostMapping("/toggle")
    public String toggleCategory(@RequestParam String identifier,
                                 RedirectAttributes redirectAttributes) {
        try {
            categoryService.toggleStatus(identifier);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Status updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Failed to update status!");
        }
        return REDIRECT_CATEGORY_LIST;
    }
}