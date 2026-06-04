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

    public static final String STOCK = "stock";
    public static final String PRODUCTS = "products";
    public static final String WAREHOUSES = "warehouses";
    public static final String MESSAGE = "message";
    public static final String STOCKS = "stocks";
    public static final String REDIRECT_STOCK_LIST = "redirect:/stock/list";

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductService productService;

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(STOCK, new StockDto());
        model.addAttribute(PRODUCTS, productService.findAllActive());
        model.addAttribute(WAREHOUSES, warehouseService.findAllActive());
        return "stock/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute(STOCK) StockDto stockDto) {
        try {
            stockService.save(stockDto);
            return REDIRECT_STOCK_LIST;

        } catch (Exception e) {
            model.addAttribute(MESSAGE, e.getMessage());
            model.addAttribute(PRODUCTS, productService.findAllActive());
            model.addAttribute(WAREHOUSES, warehouseService.findAllActive());
            return "stock/add";
        }
    }

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute(STOCKS, stockService.findAll(pageable));
        return "stock/list";
    }

    @GetMapping("/get")
    public String edit(@RequestParam Long id, Model model) {
        model.addAttribute(STOCK, stockService.findById(id));
        model.addAttribute(PRODUCTS, productService.findAllActive());
        model.addAttribute(WAREHOUSES, warehouseService.findAllActive());
        return "stock/stock";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute(STOCK) StockDto stockDto) {
        try {
            stockService.update(stockDto);
            return REDIRECT_STOCK_LIST;

        } catch (Exception e) {
            model.addAttribute(MESSAGE, e.getMessage());
            model.addAttribute(PRODUCTS, productService.findAllActive());
            model.addAttribute(WAREHOUSES, warehouseService.findAllActive());
            return "stock/stock";
        }
    }

    @GetMapping("/delete")
    public String delete(@RequestParam Long id) {
        stockService.delete(id);
        return REDIRECT_STOCK_LIST;
    }
}