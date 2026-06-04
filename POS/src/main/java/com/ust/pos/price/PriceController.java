package com.ust.pos.price;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/price")
public class PriceController {

    public static final String PRICES = "prices";
    public static final String MESSAGE = "message";
    public static final String PRICE_LIST = "price/list";

    @Autowired
    ProductService productService;

    @Autowired
    PriceService priceService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(PRICES, priceService.findAll(pageable));
        return PRICE_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute PriceDto priceDto, Pageable pageable) {
        List<ProductDto> productDtos = productService.findAll(pageable);
        model.addAttribute("productList", productDtos);
        return "price/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute PriceDto priceDto, Pageable pageable) {
        PriceDto response = priceService.save(priceDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Price Added Successfully");
        }
        model.addAttribute(PRICES, priceService.findAll(pageable));
        return PRICE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        PriceDto response = priceService.findByIdentifier(identifier);
        model.addAttribute("price", response);
        return "price/price";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute PriceDto priceDto, Pageable pageable) {
        PriceDto response = priceService.update(priceDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Updated Successfully");
        }
        model.addAttribute(PRICES, priceService.findAll(pageable));
        return PRICE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier, Pageable pageable) {
        priceService.delete(identifier);
        model.addAttribute(PRICES, priceService.findAll(pageable));
        model.addAttribute(MESSAGE, "Price deleted successfully");
        return PRICE_LIST;
    }
}
