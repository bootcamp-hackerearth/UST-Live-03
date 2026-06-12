package com.ust.pos.stock;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.rack.service.RackService;
import com.ust.pos.shelf.service.ShelfService;
import com.ust.pos.stock.service.StockService;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/stock")
public class StockController extends BaseController {

    private static final String STOCK_LIST = "stock/list";
    private static final String STOCK_ADD = "stock/add";
    private static final String STOCK_VIEW = "stock/stock";
    private static final String REDIRECT_STOCK_LIST = "redirect:/stock/list";

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductService productService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private RackService rackService;

    @Autowired
    private ShelfService shelfService;

    @GetMapping("/list")
    public String list(Model model, @ModelAttribute PaginationDto paginationDto) {
        model.addAttribute("stocks", stockService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return STOCK_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute StockDto stockDto) {
        model.addAttribute("products", productService.findByStatusTrue());
        model.addAttribute("warehouses", warehouseService.findByStatusTrue());
        model.addAttribute("racks", rackService.findActiveRacks());
        model.addAttribute("shelfs", shelfService.findActiveShelfs());
        return STOCK_ADD;
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute StockDto stockDto) {
        stockService.save(stockDto);
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/get")
    public String update(@RequestParam long id, Model model) {
        StockDto stockDto = stockService.findById(id);
        model.addAttribute("products", productService.findAll(null));
        model.addAttribute("warehouses", warehouseService.findAll(null));
        model.addAttribute("stockDto", stockDto);
        model.addAttribute("racks", rackService.findActiveRacks());
        model.addAttribute("shelfs", shelfService.findActiveShelfs());
        return STOCK_VIEW;
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute StockDto stockDto) {
        stockService.update(stockDto);
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam long id) {
        stockService.delete(id);
        return REDIRECT_STOCK_LIST;
    }
}
