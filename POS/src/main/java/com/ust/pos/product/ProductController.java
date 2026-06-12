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

    public static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";
    public static final String BRAND_LIST = "brandList";
    public static final String UNIT_LIST = "unitList";
    public static final String MODEL_LIST = "modelList";
    public static final String CATEGORY_LIST = "categoryList";

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private UnitService unitService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("products", productService.findAll(pageable));
        return "product/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ProductDto productDto) {

        model.addAttribute(CATEGORY_LIST, categoryService.findAllActive());
        model.addAttribute(BRAND_LIST, brandService.findAllActive());
        model.addAttribute(MODEL_LIST, modelService.findAllActive());
        model.addAttribute(UNIT_LIST, unitService.findAllActive());
        return "product/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ProductDto productDto) {

        ProductDto response = productService.save(productDto);

        if (!response.isSuccess()) {
            model.addAttribute(CATEGORY_LIST, categoryService.findAllActive());
            model.addAttribute(BRAND_LIST, brandService.findAllActive());
            model.addAttribute(MODEL_LIST, modelService.findAllActive());
            model.addAttribute(UNIT_LIST, unitService.findAllActive());
            model.addAttribute("error", response.getMessage());
            return "product/add";
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {

        ProductDto response = productService.findByIdentifier(identifier);
        model.addAttribute(CATEGORY_LIST, categoryService.findAllActive());
        model.addAttribute(BRAND_LIST, brandService.findAllActive());
        model.addAttribute(MODEL_LIST, modelService.findAllActive());
        model.addAttribute(UNIT_LIST, unitService.findAllActive());
        model.addAttribute("product", response);
        return "product/product";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ProductDto productDto) {

        ProductDto response = productService.update(productDto);

        if (!response.isSuccess()) {
            model.addAttribute(CATEGORY_LIST, categoryService.findAllActive());
            model.addAttribute(BRAND_LIST, brandService.findAllActive());
            model.addAttribute(MODEL_LIST, modelService.findAllActive());
            model.addAttribute(UNIT_LIST, unitService.findAllActive());
            model.addAttribute("message", response.getMessage());
            model.addAttribute("product", productDto);
            return "product/product";
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        productService.delete(identifier);
        return REDIRECT_PRODUCT_LIST;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public String toggle(@RequestParam String identifier, boolean status) {

        productService.changeStatus(identifier, status);
        return "success";
    }
}