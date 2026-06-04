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

    public static final String REDIRECT_LIST = "redirect:/stock/list";
    public static final String WAREHOUSES = "warehouses";
    public static final String PRODUCTS = "products";
    public static final String STOCKS = "stocks";

    @Autowired
    private StockService stockService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute(STOCKS, stockService.findAll(pageable));
        return "stock/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(STOCKS, new StockDto());
        model.addAttribute(PRODUCTS, productService.findIfTrue());
        model.addAttribute(WAREHOUSES, warehouseService.findIfTrue());
        return "stock/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute StockDto stockDto) {
        StockDto response = stockService.save(stockDto);

        if (!response.isSuccess()) {
            model.addAttribute(STOCKS, stockDto);
            model.addAttribute(PRODUCTS, productService.findIfTrue());
            model.addAttribute(WAREHOUSES, warehouseService.findIfTrue());
            model.addAttribute("message", response.getMessage());
            return "redirect:/stock/add";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        model.addAttribute(STOCKS, stockService.findByIdentifier(identifier));
        model.addAttribute(WAREHOUSES, warehouseService.findIfTrue());
        model.addAttribute(PRODUCTS, productService.findIfTrue());
        return "stock/edit";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute StockDto stockDto) {
        StockDto response = stockService.update(stockDto);

        if (!response.isSuccess()) {
            model.addAttribute(STOCKS, stockDto);
            model.addAttribute("message", response.getMessage());
            model.addAttribute(PRODUCTS, productService.findIfTrue());
            model.addAttribute(WAREHOUSES, warehouseService.findIfTrue());

            return "stock/edit";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        stockService.deleteByIdentifier(identifier);
        return REDIRECT_LIST;
    }

    @PostMapping("/toggle")
    public String toggleStock(@RequestParam String identifier) {
        stockService.toggleStatus(identifier);
        return REDIRECT_LIST;
    }
}