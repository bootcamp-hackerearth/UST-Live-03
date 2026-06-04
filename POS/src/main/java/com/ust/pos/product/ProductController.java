package com.ust.pos.product;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.models.service.ModelService;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.unit.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {

    public static final String PRODUCT_LIST = "product/list";
    public static final String PRODUCTS = "products";
    public static final String MESSAGE = "message";

    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;

    @Autowired
    BrandService brandService;

    @Autowired
    UnitService unitService;

    @Autowired
    ModelService modelService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(PRODUCTS, productService.findAll(pageable));
        return PRODUCT_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ProductDto productDto) {
        model.addAttribute("categories", categoryService.findAllCategoriesWithNoSuper());
        model.addAttribute("productDto", new ProductDto());
        model.addAttribute("unit", unitService.findActiveUnit());
        model.addAttribute("brand", brandService.findActiveBrands());
        model.addAttribute("model", modelService.findActiveModels());
        return "product/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ProductDto productDto, Pageable pageable) {
        ProductDto response = productService.save(productDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Product Added Successfully");
        }
        model.addAttribute(PRODUCTS, productService.findAll(pageable));
        return PRODUCT_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ProductDto response = productService.findByIdentifier(identifier);
        model.addAttribute("categories", categoryService.findAllCategoriesWithNoSuper());
        model.addAttribute("productDto", new ProductDto());
        model.addAttribute("unit", unitService.findActiveUnit());
        model.addAttribute("brand", brandService.findActiveBrands());
        model.addAttribute("model", modelService.findActiveModels());
        model.addAttribute("product", response);
        return "product/product";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto response = productService.update(productDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            return "product/product";
        } else {
            model.addAttribute(MESSAGE, "Updated Successfully");
        }

        return "redirect:/product/list";
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier, Pageable pageable) {
        productService.delete(identifier);
        model.addAttribute(PRODUCTS, productService.findAll(pageable));
        model.addAttribute(MESSAGE, "Product deleted successfully");
        return PRODUCT_LIST;
    }
}
