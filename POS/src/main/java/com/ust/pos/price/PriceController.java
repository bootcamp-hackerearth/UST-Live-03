package com.ust.pos.price;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/price")
@Controller
public class PriceController {
    public static final String REDIRECT_ROLE_LIST = "redirect:/price/list";
    public static final String ROLE_ADD = "price/add";
    @Autowired
    private PriceService priceService;

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("prices", priceService.findAll(pageable));
        return "price/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute PriceDto priceDto, Pageable pageable) {
        model.addAttribute("products", productService.findAll(pageable));
        return ROLE_ADD;
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute PriceDto priceDto) {
        PriceDto response = priceService.save(priceDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return ROLE_ADD;
        }
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        PriceDto response = priceService.findByIdentifier(identifier);
        model.addAttribute("priceDto", response);
        return "price/price";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute PriceDto priceDto) {
        PriceDto response = priceService.update(priceDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return ROLE_ADD;
        }
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        priceService.delete(identifier);
        return REDIRECT_ROLE_LIST;
    }
}
