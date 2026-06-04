package com.ust.pos.stock;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
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
public class StockController extends BaseController {

    private static final String VIEW_STOCK_ADD = "stock/add";
    private static final String VIEW_STOCK_EDIT = "stock/stock";
    private static final String REDIRECT_STOCK_LIST = "redirect:/stock/list";

    private static final String ATTR_STOCKS = "stocks";
    private static final String ATTR_STOCK = "stock";
    private static final String ATTR_STOCK_DTO = "stockDto";
    private static final String ATTR_PRODUCTS = "products";
    private static final String ATTR_WAREHOUSES = "warehouses";
    private static final String ATTR_MESSAGE = "message";
    private static final String ATTR_MESSAGE_TYPE = "messageType";

    private static final String MESSAGE_TYPE_ERROR = "error";

    private static final String MSG_STOCK_NOT_FOUND = "Stock not found";

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductService productService;

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/list")
    public String home(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute(ATTR_STOCKS, stockService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "stock/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(ATTR_STOCK_DTO, new StockDto());
        model.addAttribute(ATTR_WAREHOUSES, warehouseService.findIfTrue());
        model.addAttribute(ATTR_PRODUCTS, productService.findIfTrue());
        return VIEW_STOCK_ADD;
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute StockDto stockDto, Model model) {
        StockDto response = stockService.createStock(stockDto);

        if (!response.isSuccess()) {
            model.addAttribute(ATTR_STOCK_DTO, stockDto);
            model.addAttribute(ATTR_WAREHOUSES, warehouseService.findIfTrue());
            model.addAttribute(ATTR_PRODUCTS, productService.findIfTrue());
            model.addAttribute(ATTR_MESSAGE, response.getMessage());
            model.addAttribute(ATTR_MESSAGE_TYPE, MESSAGE_TYPE_ERROR);
            return VIEW_STOCK_ADD;
        }

        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/get")
    public String update(@RequestParam Long productId, @RequestParam Long warehouseId, Model model) {

        StockDto response = stockService.getStock(productId, warehouseId);

        if (!response.isSuccess()) {
            model.addAttribute(ATTR_MESSAGE, response.getMessage());
            model.addAttribute(ATTR_MESSAGE_TYPE, MESSAGE_TYPE_ERROR);
            return REDIRECT_STOCK_LIST;
        }

        model.addAttribute(ATTR_STOCK, response);
        model.addAttribute(ATTR_WAREHOUSES, warehouseService.findIfTrue());
        model.addAttribute(ATTR_PRODUCTS, productService.findIfTrue());
        return VIEW_STOCK_EDIT;
    }

    @PostMapping("/update")
    public String updatePost(@RequestParam Long stockId, @RequestParam Integer quantity, Model model) {

        StockDto response = stockService.updateStockQuantity(stockId, quantity);

        if (!response.isSuccess()) {
            model.addAttribute(ATTR_MESSAGE, response.getMessage());
            model.addAttribute(ATTR_MESSAGE_TYPE, MESSAGE_TYPE_ERROR);
        }

        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam Long id, Model model) {
        boolean deleted = stockService.deleteStock(id);

        if (!deleted) {
            model.addAttribute(ATTR_MESSAGE, MSG_STOCK_NOT_FOUND);
            model.addAttribute(ATTR_MESSAGE_TYPE, MESSAGE_TYPE_ERROR);
        }

        return REDIRECT_STOCK_LIST;
    }

    @PostMapping("/toggle-status")
    @ResponseBody
    public void toggleStatus(@RequestParam Long id) {
        stockService.toggleStatus(id);
    }
}