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

    public static final String REDIRECT_ROLE_LIST = "redirect:/product/list";
    public static final String MODEL = "model";
    public static final String BRAND = "brand";

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ModelService modelService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {

        model.addAttribute("products", productService.findAll(pageable));
        return "product/list";
    }

    @GetMapping("/add")
    public String add(Model model) {

        model.addAttribute("categories", categoryService.findAllCategoriesWithNoSuper(null));
        model.addAttribute("productDto", new ProductDto());
        model.addAttribute("unit", unitService.findActiveUnit());
        model.addAttribute(BRAND, brandService.findActiveBrands());
        model.addAttribute(MODEL, modelService.findActiveModels());

        return "product/add";
    }

    @PostMapping("/add")

    public String addPost(Model model, @ModelAttribute ProductDto productDto) {

        ProductDto response = productService.save(productDto);
        model.addAttribute("unit", unitService.findActiveUnit());
        model.addAttribute(BRAND, brandService.findActiveBrands());
        model.addAttribute(MODEL, modelService.findActiveModels());

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "product/add";
        }

        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {

        ProductDto response = productService.findByIdentifier(identifier);
        model.addAttribute("categories", categoryService.findAllCategoriesWithNoSuper(null));
        model.addAttribute("productDto", response);
        model.addAttribute("unit", unitService.findActiveUnit());
        model.addAttribute(BRAND, brandService.findActiveBrands());
        model.addAttribute(MODEL, modelService.findActiveModels());

        return "product/product";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ProductDto productDto) {

        ProductDto response = productService.update(productDto);
        model.addAttribute("unit", unitService.findActiveUnit());
        model.addAttribute(BRAND, brandService.findActiveBrands());
        model.addAttribute(MODEL, modelService.findActiveModels());

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "product/product";
        }

        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {

        productService.delete(identifier);
        return REDIRECT_ROLE_LIST;
    }
}

