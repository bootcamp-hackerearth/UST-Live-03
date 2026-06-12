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

    public static final String REDIRECT_LIST = "redirect:/price/list";
    public static final String PRICES = "prices";

    @Autowired
    private PriceService priceService;
    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute(PRICES, priceService.findAll(pageable));
        return "price/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(PRICES, new PriceDto());
        model.addAttribute("products", productService.findIfTrue());
        return "price/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute PriceDto dto) {
        PriceDto response = priceService.save(dto);
        if (!response.isSuccess()) {
            model.addAttribute(PRICES, dto);
            model.addAttribute("message", response.getMessage());
            return "price/add";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        model.addAttribute(PRICES, priceService.findByIdentifier(identifier));
        model.addAttribute("products", productService.findIfTrue());
        return "price/edit";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute PriceDto dto, Pageable pageable) {
        PriceDto response = priceService.update(dto);
        if (!response.isSuccess()) {
            model.addAttribute(PRICES, dto);

            model.addAttribute("message", response.getMessage());
            return "price/edit";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        priceService.delete(identifier);
        return REDIRECT_LIST;
    }
}