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

    private static final String WAREHOUSES = "warehouses";
    private static final String PRODUCTS = "products";
    private static final String REDIRECT_STOCK_LIST = "redirect:/stock/list";
    @Autowired
    private StockService stockService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute("stocks", stockService.findAll());
        return "stock/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute StockDto stockDto) {
        model.addAttribute(WAREHOUSES, warehouseService.findAll());
        model.addAttribute(PRODUCTS, productService.findAll());
        return "stock/add";
    }

    @PostMapping("/add")
    public String addStock(Model model, @ModelAttribute StockDto stockDto) {
        model.addAttribute(WAREHOUSES, warehouseService.findAll());
        model.addAttribute(PRODUCTS, productService.findAll());
        stockDto.setStatus(stockDto.getQuantity() != 0);
        StockDto stockDto1 = stockService.save(stockDto);
        if (!stockDto1.isSuccess()) {
            model.addAttribute("message", stockDto1.getMessage());
            stockDto1.setStatus(false);
            return "stock/add";
        }
        stockDto1.setStatus(true);
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        model.addAttribute(WAREHOUSES, warehouseService.findAll());
        model.addAttribute(PRODUCTS, productService.findAll());
        StockDto stockDto = stockService.findByIdentifier(identifier);
        model.addAttribute("stock", stockDto);
        return "stock/stock";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute StockDto stockDto) {
        model.addAttribute(WAREHOUSES, warehouseService.findAll());
        model.addAttribute(PRODUCTS, productService.findAll());
        stockDto.setStatus(stockDto.getQuantity() != 0);
        StockDto stockDto1 = stockService.update(stockDto);
        if (!stockDto1.isSuccess()) {
            model.addAttribute("message", stockDto1.getMessage());
            stockDto1.setStatus(false);
            return "stock/update";
        }
        stockDto1.setStatus(true);
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        stockService.delete(identifier);
        return REDIRECT_STOCK_LIST;
    }
}
