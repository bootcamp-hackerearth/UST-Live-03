package com.ust.pos.stock;

import com.ust.pos.dto.StockDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stock.service.StockService;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/stock")
public class StockController {
    public static final String STOCK_DTO = "stockDto";

    private final StockService stockService;

    private final WarehouseService warehouseService;

    private final ProductService productService;

    public StockController(StockService stockService, WarehouseService warehouseService, ProductService productService) {
        this.stockService = stockService;
        this.warehouseService = warehouseService;
        this.productService = productService;
    }


    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("stockList", stockService.findAll(pageable));
        return "stock/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {

        model.addAttribute(STOCK_DTO, new StockDto());
        model.addAttribute("warehouseList", warehouseService.findAllActiveWarehouse());
        model.addAttribute("productList", productService.findAllActiveProduct());
        return "stock/add";
    }

    @PostMapping("/add")
    public String saveStock(@ModelAttribute(STOCK_DTO) StockDto stockDto, Model model) {

        StockDto response = stockService.save(stockDto);
        model.addAttribute("message", response.getMessage());
        model.addAttribute(STOCK_DTO, new StockDto());
        return "stock/add";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        stockService.delete(identifier);
        return "redirect:/stock/list";
    }

    @GetMapping("/update")
    public String showEditForm(@RequestParam Long id, Model model) {
        StockDto stockDto = stockService.findById(id);
        model.addAttribute(STOCK_DTO, stockDto);
        model.addAttribute("products", productService.findAllActiveProduct());
        model.addAttribute("warehouses", warehouseService.findAllActiveWarehouse());
        return "stock/stock";
    }

    @PostMapping("/update")
    public String editform(Model model, @ModelAttribute StockDto stockDto) {
        StockDto responseDto = stockService.update(stockDto);
        model.addAttribute(STOCK_DTO, responseDto);
        return "stock/stock";
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status) {
        stockService.changeStockStatus(identifier, status);
        return "redirect:/stock/list";
    }
}