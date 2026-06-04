package com.ust.pos.price;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/price")
public class PriceController {

    public static final String REDIRECT_PRICE_LIST = "redirect:/price/list";
    public static final String PRODUCTS = "products";

    @Autowired
    private PriceService priceService;

    @Autowired
    private ProductService productService;

    @GetMapping("/add")
    public String add(Model model, Pageable pageable) {
        model.addAttribute("price", new PriceDto());
        model.addAttribute(PRODUCTS, productService.findAll(pageable));
        return "price/add";
    }

    @PostMapping("/add")
    public String addPost(Model model,
                          @ModelAttribute("price") PriceDto priceDto, Pageable pageable) {
        PriceDto response = priceService.save(priceDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("product", priceDto);
            model.addAttribute(PRODUCTS, productService.findAll(pageable));
            return "price/add";
        }
        return REDIRECT_PRICE_LIST;
    }

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("prices", priceService.findAll(pageable));
        model.addAttribute(PRODUCTS, productService.findAll(pageable));
        return "price/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier, Pageable pageable) {
        PriceDto response = priceService.findByIdentifier(identifier);
        model.addAttribute("price", response);
        model.addAttribute(PRODUCTS, productService.findAll(pageable));
        return "price/price";
    }

    @PostMapping("/update")
    public String updatePost(Model model,
                             @ModelAttribute("price") PriceDto priceDto, Pageable pageable) {
        PriceDto response = priceService.update(priceDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(PRODUCTS, productService.findAll(pageable));
            return "price/price";
        }
        return REDIRECT_PRICE_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        priceService.delete(identifier);
        return REDIRECT_PRICE_LIST;
    }

}
