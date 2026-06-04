package com.ust.pos.price;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/price")
public class PriceController {

    public static final String REDIRECT_LIST = "redirect:/price/list";
    public static final String PRICES = "prices";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String ERROR_MESSAGE = "errorMessage";

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
    public String addPost(Model model,
                          @ModelAttribute PriceDto priceDto,
                          RedirectAttributes redirectAttributes) {

        PriceDto response = priceService.save(priceDto);

        if (!response.isSuccess()) {
            model.addAttribute(PRICES, priceDto);
            model.addAttribute("message", response.getMessage());
            return "price/add";
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Price added successfully!");
        return REDIRECT_LIST;
    }

    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        model.addAttribute(PRICES, priceService.findByIdentifier(identifier));
        model.addAttribute("products", productService.findIfTrue());
        return "price/edit";
    }

    @PostMapping("/update")
    public String update(Model model,
                         @ModelAttribute PriceDto priceDto,
                         Pageable pageable,
                         RedirectAttributes redirectAttributes) {

        PriceDto response = priceService.update(priceDto);

        if (!response.isSuccess()) {
            model.addAttribute(PRICES, priceDto);
            model.addAttribute("message", response.getMessage());
            return "price/edit";
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Price updated successfully!");
        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier,
                         RedirectAttributes redirectAttributes) {

        try {
            priceService.delete(identifier);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Price deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Failed to delete price!");
        }

        return REDIRECT_LIST;
    }
}