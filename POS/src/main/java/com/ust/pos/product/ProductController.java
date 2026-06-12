package com.ust.pos.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.models.service.ModelsService;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController extends BaseController {
    public static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";
    private static final String MESSAGE = "message";

    @Autowired
    private ProductService productService;

    @Autowired
    private PriceService priceService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ModelsService modelsService;

    @GetMapping("/add")
    public String add(Model model, Pageable pageable) {
        model.addAttribute("prices", priceService.findAll(pageable));
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("brands", brandService.findAllActive());
        model.addAttribute("models", modelsService.findAllActive());
        return "product/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto response = productService.save(productDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            return "redirect:/product/add";
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("products", productService.findAll(pageable));
        return "product/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ProductDto response = productService.findByIdentifier(identifier);
        model.addAttribute("product", response);
        PriceDto latestPrice = priceService.findByIdentifier(identifier);
        model.addAttribute("mrp", latestPrice != null ? latestPrice.getMrp() : null);
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("brands", brandService.findAllActive());
        model.addAttribute("models", modelsService.findAllActive());
        return "product/product";
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute ProductDto productDto) {
        ProductDto response = productService.update(productDto);
        if (!response.isSuccess()) {
            return REDIRECT_PRODUCT_LIST;
        }
        return "redirect:/product/get?identifier=" + productDto.getIdentifier();
    }

    @PostMapping("/toggle")
    @ResponseBody
    public void toggleStatus(@RequestParam String identifier) {
        productService.toggleStatus(identifier);
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        productService.delete(identifier);
        return REDIRECT_PRODUCT_LIST;
    }
}
