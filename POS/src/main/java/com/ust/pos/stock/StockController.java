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

    public static final String STOCKS = "stocks";
    public static final String REDIRECT_STOCK_LIST = "redirect:/stock/list";
    @Autowired
    private StockService stockService;

    @Autowired
    private ProductService productService;

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(STOCKS, stockService.findAll(pageable));
        return "stock/list";
    }

    @GetMapping("/add")
    public String add(Model model, Pageable pageable, @ModelAttribute StockDto stockDto) {
        model.addAttribute("products", productService.findAll(pageable));
        model.addAttribute("warehouses", warehouseService.findAll(pageable));
        return "stock/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, Pageable pageable, @ModelAttribute StockDto stockDto) {
        StockDto response = stockService.save(stockDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("products", productService.findAll(pageable));
            model.addAttribute(STOCKS, stockService.findAll(pageable));
            model.addAttribute("warehouses", warehouseService.findAll(pageable));
            return "stock/add";
        }
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, Pageable pageable, @RequestParam String identifier, @ModelAttribute StockDto stockDto) {
        StockDto response = stockService.findByIdentifier(identifier);
        model.addAttribute(STOCKS, stockService.findAll(pageable));
        model.addAttribute("stock", response);
        return "stock/stock";
    }

    @PostMapping("/update")
    public String updatePost(Model model, Pageable pageable, @ModelAttribute StockDto stockDto) {
        StockDto response = stockService.update(stockDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("stock", stockService.findAll(pageable));
        }
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        stockService.delete(identifier);
        return REDIRECT_STOCK_LIST;
    }
}
