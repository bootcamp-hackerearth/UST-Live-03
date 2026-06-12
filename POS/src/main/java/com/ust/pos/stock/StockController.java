package com.ust.pos.stock;

import com.ust.pos.dto.StockDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stock.service.StockService;
import com.ust.pos.unit.service.UnitService;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/stock")
public class StockController {

    public static final String PRODUCT = "product";
    public static final String WAREHOUSE = "warehouse";
    public static final String STOCK = "stock";
    public static final String REDIRECT_STOCK_LIST = "redirect:/stock/list";
    @Autowired
    StockService stockService;

    @Autowired
    WarehouseService warehouseService;

    @Autowired
    ProductService productService;

    @Autowired
    UnitService unitService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("stocks", stockService.findAll(pageable));
        return "stock/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute StockDto stockDto) {
        model.addAttribute(WAREHOUSE, warehouseService.findActiveStatus());
        model.addAttribute(PRODUCT, productService.findActiveStatus());
        model.addAttribute("units", unitService.findActiveStatus());

        return "stock/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute StockDto stockDto) {
        stockService.save(stockDto);
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        stockService.delete(identifier);
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier, Pageable pageable) {
        StockDto response = stockService.findByIdentifier(identifier);
        model.addAttribute(WAREHOUSE, warehouseService.findAll(pageable));
        model.addAttribute(PRODUCT, productService.findAll(pageable));
        model.addAttribute("units", unitService.findAll(pageable));

        model.addAttribute(STOCK, response);
        return "stock/stock";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute StockDto stockDto, Pageable pageable) {
        StockDto response = stockService.update(stockDto);
        model.addAttribute(STOCK, response);
        model.addAttribute(WAREHOUSE, warehouseService.findAll(pageable));
        model.addAttribute(PRODUCT, productService.findAll(pageable));


        if (!response.isSuccess()) {
            model.addAttribute(STOCK, response);
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status) {
        stockService.changeToggleStatus(identifier, status);
        return REDIRECT_STOCK_LIST;
    }
}
