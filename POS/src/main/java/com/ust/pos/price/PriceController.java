package com.ust.pos.price;

import com.ust.pos.api.BaseController;
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
public class PriceController extends BaseController {
    public static final String REDIRECT_PRICE_LIST = "redirect:/price/list";
    private static final String MESSAGE = "message";

    @Autowired
    private PriceService priceService;

    @Autowired
    private ProductService productService;

    @GetMapping("/add")
    public String add(Model model, Pageable pageable) {
        model.addAttribute("products", productService.findAll(pageable));
        return "price/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute PriceDto priceDto) {
        PriceDto response = priceService.save(priceDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            return "redirect:/price/add";
        }
        return REDIRECT_PRICE_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("prices", priceService.findAll(pageable));
        return "price/list";
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
            return REDIRECT_PRICE_LIST;
        }
        model.addAttribute("products", productService.findAll(pageable));
        return "redirect:/price/get?identifier=" + priceDto.getIdentifier();
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        priceService.delete(identifier);
        return REDIRECT_PRICE_LIST;
    }
}
