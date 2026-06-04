package com.ust.pos.product;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/product")
public class ProductController {

    public static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";
    public static final String CATEGORIES = "categories";
    public static final String PRODUCTS = "products";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String ERROR_MESSAGE = "errorMessage";

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute(PRODUCTS, productService.findAll(pageable));
        return "product/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(PRODUCTS, new ProductDto());
        model.addAttribute(CATEGORIES, categoryService.findChildCategories());
        return "product/add";
    }

    @PostMapping("/add")
    public String addPost(Model model,
                          @ModelAttribute ProductDto productDto,
                          RedirectAttributes redirectAttributes) {

        ProductDto response = productService.save(productDto);

        if (!response.isSuccess()) {
            model.addAttribute(PRODUCTS, productDto);
            model.addAttribute(CATEGORIES, categoryService.findChildCategories());
            model.addAttribute("message", response.getMessage());
            return "product/add";
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Product added successfully!");
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        ProductDto product = productService.findByIdentifier(identifier);
        model.addAttribute(CATEGORIES, categoryService.findChildCategories());
        model.addAttribute(PRODUCTS, product);
        return "product/edit";
    }

    @PostMapping("/update")
    public String updatePost(Model model,
                             @ModelAttribute ProductDto productDto,
                             RedirectAttributes redirectAttributes) {

        ProductDto response = productService.update(productDto);

        if (!response.isSuccess()) {
            model.addAttribute(PRODUCTS, productDto);
            model.addAttribute(CATEGORIES, categoryService.findChildCategories());
            model.addAttribute("message", response.getMessage());
            return "product/edit";
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Product updated successfully!");
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier,
                         RedirectAttributes redirectAttributes) {

        try {
            productService.delete(identifier);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Failed to delete product!");
        }

        return REDIRECT_PRODUCT_LIST;
    }

    @PostMapping("/toggle")
    public String toggleProduct(@RequestParam String identifier,
                                RedirectAttributes redirectAttributes) {

        try {
            productService.toggleStatus(identifier);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Status updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Failed to update status!");
        }

        return REDIRECT_PRODUCT_LIST;
    }
}