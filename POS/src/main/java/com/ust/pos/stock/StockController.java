package com.ust.pos.stock;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.node.service.NodeService;
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

    public static final String REDIRECT_STOCK_LIST = "redirect:/stock/list";
    public static final String NODES = "nodes";
    public static final String STOCK_ADD = "stock/add";
    public static final String PRODUCTS = "products";
    public static final String WAREHOUSES = "warehouses";

    @Autowired
    private StockService stockService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private ProductService productService;

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("stocks", stockService.findAll(pageable));
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return "stock/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute StockDto stockDto, Pageable pageable) {
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        WsDto<ProductDto> productDtos = productService.findAll(pageable);
        model.addAttribute(PRODUCTS, productDtos);
        WsDto<WarehouseDto> warehouseDtos = warehouseService.findAll(pageable);
        model.addAttribute(WAREHOUSES, warehouseDtos);
        return STOCK_ADD;
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute StockDto stockDto, Pageable pageable) {
        StockDto response = stockService.save(stockDto);
        if (!response.isSuccess()) {
            model.addAttribute(NODES, nodeService.getNodesForRoles());
            model.addAttribute("message", response.getMessage());
            WsDto<ProductDto> productDtos = productService.findAll(pageable);
            model.addAttribute(PRODUCTS, productDtos);
            WsDto<WarehouseDto> warehouseDtos = warehouseService.findAll(pageable);
            model.addAttribute(WAREHOUSES, warehouseDtos);
            return STOCK_ADD;
        }
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier, Pageable pageable) {
        StockDto response = stockService.findByIdentifier(identifier);
        model.addAttribute("stock", response);
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        WsDto<ProductDto> productDtos = productService.findAll(pageable);
        model.addAttribute(PRODUCTS, productDtos);
        WsDto<WarehouseDto> warehouseDtos = warehouseService.findAll(pageable);
        model.addAttribute(WAREHOUSES, warehouseDtos);
        return "stock/stock";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute StockDto stockDto, Pageable pageable) {
        StockDto response = stockService.update(stockDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(NODES, nodeService.getNodesForRoles());
            WsDto<ProductDto> productDtos = productService.findAll(pageable);
            model.addAttribute(PRODUCTS, productDtos);
            WsDto<WarehouseDto> warehouseDtos = warehouseService.findAll(pageable);
            model.addAttribute(WAREHOUSES, warehouseDtos);
            return "stock/stock";
        }
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        stockService.delete(identifier);
        return REDIRECT_STOCK_LIST;
    }
}
