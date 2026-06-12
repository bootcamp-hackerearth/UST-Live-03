package com.ust.pos.stock;

import com.ust.pos.dto.StockDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stock.service.StockService;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/stock")
public class StockController {
    public static final String REDIRECT = "redirect:/stock/list";
    public static final String ADD_STOCK = "stock/add";
    @Autowired
    StockService stockService;

    @Autowired
    WarehouseService warehouseService;

    @Autowired
    ProductService productService;

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute StockDto stockDto) {
        model.addAttribute("product", productService.findAll());
        model.addAttribute("warehouse", warehouseService.findAll());
        return ADD_STOCK;
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute StockDto stockDto) {
        StockDto result = stockService.save(stockDto);
        if (!result.isSuccess()) {
            model.addAttribute("message", result.getMessage());
            return ADD_STOCK;
        }
        return ADD_STOCK;
    }

    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        StockDto response = stockService.findByIdentifier(identifier);
        model.addAttribute("warehouse", warehouseService.findAll());
        model.addAttribute("stock", response);
        return ("stock/stock");
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute StockDto stockDto) {
        StockDto response = stockService.update(stockDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return REDIRECT;
        }
        return REDIRECT;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        stockService.delete(identifier);
        return REDIRECT;
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("stock", stockService.findAll());
        return ("stock/list");
    }

    @PostMapping("status")
    public String updateStatus(Model model, @RequestParam String identifier, boolean status) {
        stockService.updateStatusOnly(identifier, status);
        return "redirect:/shelf/list";
    }
}
