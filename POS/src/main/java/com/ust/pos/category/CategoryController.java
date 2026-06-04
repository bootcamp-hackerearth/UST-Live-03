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

    public static final String REDIRECT_CATEGORY_LIST = "redirect:/category/list";
    private static final String SUPERCATEGORY = "superCategories";
    private static final String MESSAGE = "message";

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String home(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("categorys", categoryService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "category/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute CategoryDto categoryDto) {
        model.addAttribute(SUPERCATEGORY, categoryService.findSuperCategories());
        model.addAttribute("categoryDto", categoryDto);
        return "category/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute CategoryDto categoryDto) {

        CategoryDto response = categoryService.save(categoryDto);

        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute(SUPERCATEGORY, categoryService.findSuperCategories());
            return "category/add";
        }

        return REDIRECT_CATEGORY_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {

        CategoryDto response = categoryService.findByIdentifier(identifier);

        if (response == null) {
            model.addAttribute(MESSAGE, "Category not found");
            return REDIRECT_CATEGORY_LIST;
        }

        model.addAttribute("category", response);

        model.addAttribute(SUPERCATEGORY, categoryService.findSuperCategories().stream().filter(sc -> !sc.getIdentifier().equals(response.getIdentifier())).toList());

        return "category/category";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute CategoryDto categoryDto) {

        CategoryDto response = categoryService.update(categoryDto);

        if (!response.isSuccess()) {
            model.addAttribute("category", response);
            model.addAttribute(SUPERCATEGORY, categoryService.findSuperCategories());
            model.addAttribute(MESSAGE, response.getMessage());
            return "category/category";
        }

        return REDIRECT_CATEGORY_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        categoryService.delete(identifier);
        return REDIRECT_CATEGORY_LIST;
    }

    @PostMapping("/toggle-status")
    @ResponseBody
    public void toggleStatus(@RequestParam String identifier) {
        categoryService.toggleStatus(identifier);
    }
}