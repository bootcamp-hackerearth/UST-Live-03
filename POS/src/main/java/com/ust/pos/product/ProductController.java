package com.ust.pos.product;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    private final WarehouseService warehouseService;

    public ProductController(ProductService productService, WarehouseService warehouseService) {
        this.productService = productService;
        this.warehouseService = warehouseService;
    }

    private static final String PRODUCTS = "products";
    private static final String WAREHOUSES = "warehouses";
    private static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";

    @GetMapping("/list")
    public String home(Model model)
    {
        model.addAttribute(PRODUCTS, productService.findAll());
        return "product/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ProductDto productDto)
    {
        model.addAttribute(WAREHOUSES, warehouseService.findAll());
        model.addAttribute(PRODUCTS, productService.listOfCategories());
        return "product/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ProductDto productDto)
    {
        ProductDto productDto1 = productService.save(productDto);
        if(!productDto1.isSuccess())
        {
            model.addAttribute("message", productDto1.getMessage());
            model.addAttribute(WAREHOUSES, warehouseService.findAll());
            return "product/add";
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier)
    {
        ProductDto productDto = productService.findByIdentifier(identifier);
        model.addAttribute(PRODUCTS, productDto);
        model.addAttribute(WAREHOUSES, warehouseService.findAll());
        model.addAttribute(PRODUCTS, productService.listOfCategories());
        return "product/product";
    }

    @PostMapping("/update")
    public String doupdate(Model model, @ModelAttribute ProductDto productDto)
    {
        ProductDto productDto1 = productService.update(productDto);
        if(!productDto1.isSuccess())
        {
            model.addAttribute("message", productDto1.getMessage());
            model.addAttribute(WAREHOUSES, warehouseService.findAll());
            return "product/update";
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier)
    {
        productService.delete(identifier);
        return REDIRECT_PRODUCT_LIST;
    }
}
