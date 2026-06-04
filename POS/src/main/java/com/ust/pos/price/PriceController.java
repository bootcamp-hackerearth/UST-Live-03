package com.ust.pos.price;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/price")
public class PriceController {
    private static final String PRODUCTS = "products";
    private static final String REDIRECT_PRICE_LIST = "redirect:/price/list";
    @Autowired
    private PriceService priceService;
    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute("prices", priceService.findAll());
        return "price/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute PriceDto priceDto) {
        model.addAttribute("price", priceService.findAll());
        model.addAttribute(PRODUCTS, productService.findAll());
        return "price/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute PriceDto priceDto) {
        PriceDto priceDto1 = priceService.save(priceDto);
        if (!priceDto1.isSuccess()) {
            model.addAttribute("message", priceDto1.getMessage());
            model.addAttribute(PRODUCTS, productService.findAll());
            return "price/add";
        }
        return REDIRECT_PRICE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        PriceDto priceDto = priceService.findByIdentifier(identifier);
        model.addAttribute(PRODUCTS, productService.findAll());
        model.addAttribute("price", priceDto);
        return "price/price";
    }

    @PostMapping("/update")
    public String doupdate(Model model, @ModelAttribute PriceDto priceDto) {
        PriceDto priceDto1 = priceService.update(priceDto);
        if (!priceDto1.isSuccess()) {
            model.addAttribute("message", priceDto1.getMessage());
            model.addAttribute(PRODUCTS, productService.findAll());
            return "price/update";
        }
        return REDIRECT_PRICE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        priceService.delete(identifier);
        return REDIRECT_PRICE_LIST;
    }
}
