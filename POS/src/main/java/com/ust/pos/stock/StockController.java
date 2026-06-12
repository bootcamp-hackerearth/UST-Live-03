package com.ust.pos.stock;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
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

    public static final String PRODUCTS = "products";
    public static final String REDIRECT_STOCK_LIST = "redirect:/stock/list";
    public static final String WAREHOUSES = "warehouses";

    @Autowired
    ProductService productService;

    @Autowired
    StockService stockService;


    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute StockDto stockDto, Pageable pageable) {
        model.addAttribute(PRODUCTS, productService.findAll(pageable));
        model.addAttribute(WAREHOUSES, warehouseService.findAll(pageable));
        return "stock/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute StockDto stockDto,Pageable pageable) {
        String identifier = stockDto.getProduct() + "_" + stockDto.getWarehouse();
        stockDto.setIdentifier(identifier);
        StockDto response = stockService.save(stockDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("stockDto", stockDto);
            model.addAttribute(PRODUCTS, productService.findAll(pageable));
            model.addAttribute(WAREHOUSES, warehouseService.findAll(pageable));
            return "stock/add";
        }
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, @ModelAttribute PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),paginationDto.getSortField());
        model.addAttribute("stocks", stockService.findAll(pageable));
        return "stock/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier, Pageable pageable) {
        StockDto response = stockService.findByIdentifier(identifier);
        model.addAttribute("stock", response);
        model.addAttribute(PRODUCTS, productService.findAll(pageable));
        model.addAttribute(WAREHOUSES, warehouseService.findAll(pageable));
        return "stock/stock";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute StockDto stockDto) {
        StockDto response = stockService.update(stockDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        stockService.delete(identifier);
        return REDIRECT_STOCK_LIST;
    }

    @PostMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier,@RequestParam boolean status) {
        stockService.toggleStatus(identifier,status);
        return REDIRECT_STOCK_LIST;
    }
}