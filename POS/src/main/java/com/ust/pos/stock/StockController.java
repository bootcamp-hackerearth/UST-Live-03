package com.ust.pos.stock;

import com.ust.pos.dto.StockDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stock.service.StockService;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/stock")
public class StockController {

    public static final String REDIRECT_STOCK_LIST = "redirect:/stock/list";
    public static final String PRODUCTS = "products";
    public static final String WAREHOUSES = "warehouses";

    @Autowired
    StockService stockService;

    @Autowired
    ProductService productService;

    @Autowired
    WarehouseService warehouseService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {

        model.addAttribute("stocks", stockService.findAll(pageable));
        return "stock/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute StockDto stockDto) {

        model.addAttribute(PRODUCTS, productService.findAllActive());
        model.addAttribute(WAREHOUSES, warehouseService.findAllActive());
        return "stock/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute StockDto stockDto) {

        StockDto response = stockService.save(stockDto);

        if (!response.isSuccess()) {
            model.addAttribute(PRODUCTS, productService.findAllActive());
            model.addAttribute(WAREHOUSES, warehouseService.findAllActive());
            model.addAttribute("error", response.getMessage());
            return "stock/add";
        }
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {

        StockDto response = stockService.findByIdentifier(identifier);
        model.addAttribute(PRODUCTS, productService.findAllActive());
        model.addAttribute(WAREHOUSES, warehouseService.findAllActive());
        model.addAttribute("stock", response);
        return "stock/stock";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute StockDto stockDto) {

        StockDto response = stockService.update(stockDto);

        if (!response.isSuccess()) {
            model.addAttribute(PRODUCTS, productService.findAllActive());
            model.addAttribute(WAREHOUSES, warehouseService.findAllActive());
            model.addAttribute("message", response.getMessage());
            model.addAttribute("stock", stockDto);
            return "stock/stock";
        }
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {

        stockService.delete(identifier);
        return REDIRECT_STOCK_LIST;
    }
}
