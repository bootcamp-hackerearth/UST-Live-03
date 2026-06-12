package com.ust.pos.stock;

import com.ust.pos.dto.StockDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/stock")
@Controller
public class StockController {
    public static final String REDIRECT_ROLE_LIST = "redirect:/stock/list";
    public static final String ROLE_ADD = "stock/add";

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("stocks", stockService.findAll(pageable));
        return "stock/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute StockDto stockDto, Pageable pageable) {
        model.addAttribute("products", productService.findAll(pageable));
        return ROLE_ADD;
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute StockDto stockDto) {
        StockDto response = stockService.save(stockDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return ROLE_ADD;
        }
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        StockDto response = stockService.findByIdentifier(identifier);
        model.addAttribute("stockDto", response);
        return "stock/stock";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute StockDto stockDto) {
        StockDto response = stockService.update(stockDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return ROLE_ADD;
        }
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        stockService.delete(identifier);
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        stockService.toggleStatus(identifier);
        return REDIRECT_ROLE_LIST;
    }
}
