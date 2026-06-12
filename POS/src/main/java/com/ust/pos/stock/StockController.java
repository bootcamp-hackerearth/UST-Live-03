package com.ust.pos.stock;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stock.service.StockService;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/stock")
public class StockController {

    public static final String STOCKS = "stocks";
    public static final String STOCKS1 = STOCKS;
    public static final String STOCK_LIST = "stock/list";
    public static final String MESSAGE = "message";
    @Autowired
    StockService stockService;

    @Autowired
    ProductService productService;

    @Autowired
    WarehouseService warehouseService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(STOCKS1, stockService.findAll(pageable));
        return STOCK_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute StockDto stockDto, Pageable pageable) {
        WsDto<ProductDto> productDtos = productService.findAll(pageable);
        model.addAttribute("productList", productDtos);
        WsDto<WarehouseDto> warehouseDtos = warehouseService.findAll(pageable);
        model.addAttribute("warehouseList", warehouseDtos);
        return "stock/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute StockDto stockDto, Pageable pageable) {
        StockDto response = stockService.save(stockDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Stock Added Successfully");
        }
        model.addAttribute(STOCKS, stockService.findAll(pageable));
        return STOCK_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier, Pageable pageable) {
        WsDto<ProductDto> productDtos = productService.findAll(pageable);
        model.addAttribute("productList", productDtos);
        WsDto<WarehouseDto> warehouseDtos = warehouseService.findAll(pageable);
        model.addAttribute("warehouseList", warehouseDtos);
        StockDto response = stockService.findByIdentifier(identifier);
        model.addAttribute("stock", response);
        return "stock/stock";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute StockDto stockDto, Pageable pageable) {
        StockDto response = stockService.update(stockDto);

        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Updated Successfully");
        }
        model.addAttribute(STOCKS, stockService.findAll(pageable));
        return STOCK_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier, Pageable pageable) {
        stockService.delete(identifier);
        model.addAttribute(STOCKS, stockService.findAll(pageable));
        model.addAttribute(MESSAGE, "Stock deleted successfully");
        return STOCK_LIST;
    }
}
