package com.ust.pos.stock;

import com.ust.pos.dto.StockDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.rack.service.RackService;
import com.ust.pos.shelf.service.ShelfService;
import com.ust.pos.stock.service.StockService;
import com.ust.pos.warehouse.service.WareHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/stock")
public class StockController {

    public static final String REDIRECT_STOCK_LIST = "redirect:/stock/list";
    public static final String PRODUCT = "product";
    public static final String WAREHOUSE = "warehouse";
    public static final String MESSAGE = "message";

    @Autowired
    private StockService stockService;
    @Autowired
    private WareHouseService wareHouseService;
    @Autowired
    private ProductService productService;
    @Autowired
    private RackService rackService;
    @Autowired
    private ShelfService shelfService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("stocks", stockService.findAll(pageable));
        return "stock/list";
    }

    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("stockDto", new StockDto());
        model.addAttribute(WAREHOUSE, wareHouseService.findActiveWareHouse());
        model.addAttribute(PRODUCT, productService.findActiveProducts());
        model.addAttribute("racks", rackService.findActiveRacks());
        model.addAttribute("shelves", shelfService.findActiveShelves());
        return "stock/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute("stockDto") StockDto stockDto) {
        String identifier = stockDto.getProduct() + "_" + stockDto.getWareHouse();
        stockDto.setIdentifier(identifier);
        StockDto response = stockService.save(stockDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute(WAREHOUSE, wareHouseService.findAll(Pageable.unpaged()));
            model.addAttribute(PRODUCT, productService.findAll(Pageable.unpaged()));
            return "stock/add";
        }
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/get")
    public String updatePage(Model model, @RequestParam String identifier) {
        StockDto stockDto = stockService.findByIdentifier(identifier);
        model.addAttribute(WAREHOUSE, wareHouseService.findActiveWareHouse());
        model.addAttribute(PRODUCT, productService.findActiveProducts());
        model.addAttribute("racks", rackService.findActiveRacks());
        model.addAttribute("shelves", shelfService.findActiveShelves());
        if (stockDto == null) {
            model.addAttribute(MESSAGE, "Stock not found");
            return REDIRECT_STOCK_LIST;
        }
        model.addAttribute("stockDto", stockDto);
        return "stock/stock";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute("stockDto") StockDto stockDto) {
        StockDto response = stockService.update(stockDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute(WAREHOUSE, wareHouseService.findAll(Pageable.unpaged()));
            return "stock/stock";
        }
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        stockService.delete(identifier);
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier, boolean status) {
        stockService.toggleStatus(identifier, status);
        return REDIRECT_STOCK_LIST;
    }
}