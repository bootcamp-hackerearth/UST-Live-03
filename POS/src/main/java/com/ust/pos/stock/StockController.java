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

    public static final String STOCK_DTO = "stockDto";
    public static final String MESSAGE = "message";
    public static final String WAREHOUSE = "warehouse";
    public static final String PRODUCT = "product";
    private static final String REDIRECT = "redirect:/stock/list";
    @Autowired
    private StockService stockService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private ProductService productService;


    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("stocks", stockService.findAll(pageable));
        return "stock/list";
    }

    @GetMapping("/add")
    public String add(Model model, Pageable pageable, @ModelAttribute StockDto stockDto) {
        model.addAttribute(WAREHOUSE, warehouseService.findAll(pageable));
        model.addAttribute(PRODUCT, productService.findActiveProducts());
        return "stock/add";
    }

    @PostMapping("/add")
    public String save(@ModelAttribute StockDto dto, Model model, Pageable pageable) {
        StockDto res = stockService.save(dto);
        if (!res.isSuccess()) {
            model.addAttribute(MESSAGE, res.getMessage());
            model.addAttribute(STOCK_DTO, dto);
            model.addAttribute(WAREHOUSE, warehouseService.findAll(pageable));
            model.addAttribute(PRODUCT, productService.findActiveProducts());
            return "stock/add";
        }
        return REDIRECT;
    }

    @GetMapping("/get")
    public String edit(@RequestParam String identifier, Model model, Pageable pageable) {
        StockDto res = stockService.findByIdentifier(identifier);
        if (!res.isSuccess()) {
            model.addAttribute(MESSAGE, res.getMessage());
            return REDIRECT;
        }
        model.addAttribute(STOCK_DTO, res);
        model.addAttribute(WAREHOUSE, warehouseService.findAll(pageable));
        model.addAttribute(PRODUCT, productService.findActiveProducts());
        return "stock/stock";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute StockDto dto, Model model, Pageable pageable) {
        StockDto res = stockService.update(dto);
        if (!res.isSuccess()) {
            model.addAttribute(MESSAGE, res.getMessage());
            model.addAttribute(STOCK_DTO, dto);
            model.addAttribute(WAREHOUSE, warehouseService.findAll(pageable));
            model.addAttribute(PRODUCT, productService.findActiveProducts());
            return "stock/stock";
        }
        return REDIRECT;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        stockService.delete(identifier);
        return REDIRECT;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        stockService.toggleStatus(identifier);
        return REDIRECT;
    }
}