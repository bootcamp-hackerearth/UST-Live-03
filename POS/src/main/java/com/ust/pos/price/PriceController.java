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

    public static final String PRICE_DTO = "priceDto";
    public static final String MESSAGE = "message";
    private static final String REDIRECT = "redirect:/price/list";
    @Autowired
    private PriceService priceService;

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("prices", priceService.findAll(pageable));
        return "price/list";
    }

    @GetMapping("/add")
    public String add(Model model, Pageable pageable) {
        model.addAttribute(PRICE_DTO, new PriceDto());
        model.addAttribute("products", productService.findAll(pageable));
        return "price/add";
    }

    @PostMapping("/add")
    public String save(@ModelAttribute PriceDto priceDto, Model model, Pageable pageable) {
        priceDto.setIdentifier(priceDto.getProductId());
        PriceDto response = priceService.save(priceDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute(PRICE_DTO, priceDto);
            model.addAttribute("products", productService.findAll(pageable));
            return "price/add";
        }
        return REDIRECT;
    }

    @GetMapping("/get")
    public String edit(@RequestParam String identifier, Model model, Pageable pageable) {
        PriceDto response = priceService.findByIdentifier(identifier);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute("prices", priceService.findAll(pageable));
            return "price/list";
        }
        model.addAttribute(PRICE_DTO, response);
        return "price/price";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute PriceDto priceDto, Model model) {
        PriceDto response = priceService.update(priceDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute(PRICE_DTO, priceDto);
            return "price/price";
        }
        return REDIRECT;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        priceService.delete(identifier);
        return REDIRECT;
    }
}