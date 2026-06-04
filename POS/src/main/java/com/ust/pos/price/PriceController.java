package com.ust.pos.price;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/price")
public class PriceController {
    public static final String REDIRECT_PRICE_LIST = "redirect:/price/list";
    public static final String PRICE = "price";
    public static final String PRODUCT = "product";

    @Autowired
    PriceService priceService;

    @Autowired
    ProductService productService;

    @GetMapping("/list")
    public String home(Model model,Pageable pageable) {
        model.addAttribute(PRICE, priceService.findAll(pageable));
        return "price/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute PriceDto priceDto) {
        model.addAttribute(PRICE,priceDto);
        model.addAttribute(PRODUCT,productService.findActiveStatus());
        return "price/add";
    }

    @PostMapping("/add")
    public String addprice(Model model, @ModelAttribute PriceDto priceDto) {
        priceService.save(priceDto);
        model.addAttribute(PRICE,priceDto);
        return REDIRECT_PRICE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        priceService.delete(identifier);
        return REDIRECT_PRICE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier,Pageable pageable) {
        PriceDto response = priceService.findByIdentifier(identifier);
        model.addAttribute(PRODUCT,productService.findAll(pageable));
        model.addAttribute(PRICE, response);
        return "price/price";
    }

    @PostMapping("/update")
    public String updatePrice(Model model, @ModelAttribute PriceDto priceDto,Pageable pageable) {
        PriceDto response = priceService.update(priceDto);
        model.addAttribute(PRICE,response);
        model.addAttribute(PRODUCT,productService.findAll(pageable));
        if (!response.isSuccess()) {
            model.addAttribute(PRICE,response);
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_PRICE_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status){
        priceService.changeToggleStatus(identifier,status);
        return REDIRECT_PRICE_LIST;
    }
}
