package com.ust.pos.product;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stock.service.StockService;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {
    private static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";
    @Autowired
    private ProductService productService;

    @Autowired
    private StockService stockService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute("products", productService.findAll());
        return "product/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ProductDto productDto) {
        model.addAttribute("categories", categoryService.findAllWithoutNull());
        model.addAttribute("stock", stockService.findAll());
        model.addAttribute("warehouse", warehouseService.findAll());
        return "product/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto productDto1 = productService.save(productDto);
        if (!productDto1.isSuccess()) {
            model.addAttribute("message", productDto1.getMessage());
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ProductDto productDto = productService.findByIdentifier(identifier);
        model.addAttribute("warehouse", warehouseService.findAll());
        model.addAttribute("categories", categoryService.findAllWithoutNull());
        model.addAttribute("product", productDto);
        return "product/product";
    }

    @PostMapping("/update")
    public String doupdate(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto productDto1 = productService.update(productDto);
        if (!productDto1.isSuccess()) {
            model.addAttribute("message", productDto1.getMessage());
            return "product/update";
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        productService.delete(identifier);
        return REDIRECT_PRODUCT_LIST;
    }
}
