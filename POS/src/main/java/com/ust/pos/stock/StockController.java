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
    private static final String REDIRECT_LIST = "redirect:/stock/list";
    @Autowired
    StockService stockService;
    @Autowired
    ProductService productService;
    @Autowired
    WarehouseService warehouseService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("stock", stockService.findAll());
        return "stock/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute StockDto stockDto) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("warehouses", warehouseService.findAll());
        return "stock/add";
    }

    @PostMapping("/add")
    public String addData(Model model, @ModelAttribute StockDto stockDto) {
        StockDto response = stockService.save(stockDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "stock/add";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        StockDto response = stockService.findByIdentifier(identifier);
        model.addAttribute("stock", response);
        model.addAttribute("warehouses", warehouseService.findAll());
        return "stock/stock";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute StockDto stockDto) {
        StockDto response = stockService.update(stockDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "/stock/stock";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        stockService.delete(identifier);
        return REDIRECT_LIST;
    }
}