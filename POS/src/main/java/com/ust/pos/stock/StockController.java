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

    public static final String PRODUCTS = "products";
    public static final String WAREHOUSES = "warehouses";
    public static final String REDIRECT_STOCK_LIST = "redirect:/stock/list";

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductService productService;

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/add")
    public String add(Model model, Pageable pageable) {
        model.addAttribute("stock", new StockDto());
        model.addAttribute(PRODUCTS, productService.findAll(pageable));
        model.addAttribute(WAREHOUSES, warehouseService.findAll(pageable));
        return "stock/add";
    }

    @PostMapping("/add")
    public String addPost(Model model,
                          @ModelAttribute("stock") StockDto stockDto, Pageable pageable) {
        try {
            stockService.save(stockDto);
            return REDIRECT_STOCK_LIST;
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute(PRODUCTS, productService.findAll(pageable));
            model.addAttribute(WAREHOUSES, warehouseService.findAll(pageable));
            return "stock/add";
        }
    }

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("stocks", stockService.findAll(pageable));
        return "stock/list";
    }

    @GetMapping("/get")
    public String edit(@RequestParam Long id, Model model, Pageable pageable) {
        StockDto stock = stockService.findById(id);
        model.addAttribute("stock", stock);
        model.addAttribute(PRODUCTS, productService.findAll(pageable));
        model.addAttribute(WAREHOUSES, warehouseService.findAll(pageable));
        return "stock/stock";
    }

    @PostMapping("/update")
    public String updatePost(Model model,
                             @ModelAttribute("stock") StockDto stockDto, Pageable pageable) {
        try {
            stockService.update(stockDto);
            return REDIRECT_STOCK_LIST;
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute(PRODUCTS, productService.findAll(pageable));
            model.addAttribute(WAREHOUSES, warehouseService.findAll(pageable));
            return "stock/stock";
        }
    }

    @GetMapping("/delete")
    public String delete(@RequestParam Long id) {
        stockService.delete(id);
        return REDIRECT_STOCK_LIST;
    }

}