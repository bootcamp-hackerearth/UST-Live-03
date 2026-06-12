package com.ust.pos.category;


import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.node.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/category")
public class CategoryController {

    public static final String REDIRECT_CATEGORY_LIST = "redirect:/category/list";
    public static final String NODES = "nodes";
    public static final String CATEGORY_ADD = "category/add";
    public static final String CATEGORYS = "categorys";

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private NodeService nodeService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(CATEGORYS, categoryService.findAll(pageable));
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return "category/list";
    }

    @GetMapping("/add")
    public String add(Model model, Pageable pageable, @ModelAttribute CategoryDto categoryDto) {
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        model.addAttribute(CATEGORYS, categoryService.findAll(pageable));
        return CATEGORY_ADD;
    }

    @PostMapping("/add")
    public String addPost(Model model, Pageable pageable, @ModelAttribute CategoryDto categoryDto) {
        CategoryDto response = categoryService.save(categoryDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(CATEGORYS, categoryService.findAll(pageable));
            model.addAttribute(NODES, nodeService.getNodesForRoles());
            return CATEGORY_ADD;
        }
        return REDIRECT_CATEGORY_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier, Pageable pageable) {
        CategoryDto response = categoryService.findByIdentifier(identifier);
        model.addAttribute("category", response);
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        model.addAttribute(CATEGORYS, categoryService.findAll(pageable));
        return "category/category";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute CategoryDto userDto, Pageable pageable) {
        CategoryDto response = categoryService.update(userDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(NODES, nodeService.getNodesForRoles());
            model.addAttribute(CATEGORYS, categoryService.findAll(pageable));
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
