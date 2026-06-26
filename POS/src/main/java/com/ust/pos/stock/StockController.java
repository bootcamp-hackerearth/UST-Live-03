package com.ust.pos.stock;

import com.ust.pos.dto.StockDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stock.service.StockService;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/stock")
public class StockController {

    public static final String REDIRECT_STOCK_LIST = "redirect:/stock/list";
    public static final String WAREHOUSE = "warehouse";
    public static final String PRODUCT = "product";

    private final StockService stockService;
    private final ProductService productService;
    private final WarehouseService warehouseService;

    public StockController(StockService stockService,
                           ProductService productService,
                           WarehouseService warehouseService) {
        this.stockService = stockService;
        this.productService = productService;
        this.warehouseService = warehouseService;
    }

    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute("stocks", stockService.findAll());
        return "stock/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute StockDto stockDto) {
        model.addAttribute(PRODUCT, productService.findAll());
        model.addAttribute(WAREHOUSE, warehouseService.findAll());
        return "stock/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute StockDto stockDto) {
        StockDto response = stockService.save(stockDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(PRODUCT, productService.findAll());
            model.addAttribute(WAREHOUSE, warehouseService.findAll());
            return "stock/add";
        }

        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        StockDto response = stockService.findByIdentifier(identifier);

        model.addAttribute("stock", response);
        model.addAttribute(PRODUCT, productService.findAll());
        model.addAttribute(WAREHOUSE, warehouseService.findAll());

        return "stock/stock";
    }

    @PutMapping("/update")
    public String updatePost(Model model, @ModelAttribute StockDto stockDto) {
        StockDto response = stockService.update(stockDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("stock", stockDto);
            model.addAttribute(PRODUCT, productService.findAll());
            model.addAttribute(WAREHOUSE, warehouseService.findAll());
            return "stock/stock";
        }

        return REDIRECT_STOCK_LIST;
    }

    @DeleteMapping("/delete")
    public String delete(@RequestParam String identifier) {
        stockService.delete(identifier);
        return REDIRECT_STOCK_LIST;
    }
}