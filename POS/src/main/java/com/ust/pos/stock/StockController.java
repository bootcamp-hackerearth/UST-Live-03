package com.ust.pos.stock;

import com.ust.pos.api.BaseController;
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
public class StockController extends BaseController {
    public static final String REDIRECT_STOCK_LIST = "redirect:/stock/list";
    private static final String MESSAGE = "message";

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductService productService;

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/add")
    public String add(Model model, Pageable pageable) {
        model.addAttribute("products", productService.findAll(pageable));
        model.addAttribute("warehouses", warehouseService.findAllActive());
        return "stock/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute StockDto stockDto) {
        StockDto response = stockService.save(stockDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            return "redirect:/stock/add";
        }
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("stocks", stockService.findAll(pageable));
        return "stock/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        StockDto response = stockService.findByIdentifier(identifier);
        model.addAttribute("stock", response);
        model.addAttribute("products", productService.findAllActive());
        model.addAttribute("warehouses", warehouseService.findAllActive());
        return "stock/stock";
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute StockDto stockDto) {
        StockDto response = stockService.update(stockDto);
        if (!response.isSuccess()) {
            return REDIRECT_STOCK_LIST;
        }
        return "redirect:/stock/get?identifier=" + stockDto.getIdentifier();
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        stockService.delete(identifier);
        return REDIRECT_STOCK_LIST;
    }
}
