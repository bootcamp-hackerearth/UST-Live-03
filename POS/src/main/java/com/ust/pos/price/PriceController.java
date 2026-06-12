package com.ust.pos.price;


import com.ust.pos.dto.PriceDto;
import com.ust.pos.price.service.PriceService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/price")
public class PriceController {
    public static final String PRICE_DTO = "priceDto";
    @Autowired
    PriceService priceService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("priceList", priceService.findAll(pageable));
        return "price/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(PRICE_DTO, new PriceDto());
        return "price/add";
    }

    @PostMapping("/add")
    public String save(Model model, @ModelAttribute PriceDto priceDto) {

        PriceDto response = priceService.save(priceDto);
        model.addAttribute("message", response.getMessage());
        model.addAttribute(PRICE_DTO, new PriceDto());
        return "price/add";
    }

    @Transactional
    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        priceService.delete(identifier);
        return "redirect:/price/list";
    }

    @GetMapping("/update")
    public String update(Model model, @RequestParam String identifier) {
        PriceDto response = priceService.findByIdentifier(identifier);
        model.addAttribute(PRICE_DTO, response);
        return "price/price";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute PriceDto priceDto) {

        PriceDto response = priceService.update(priceDto);
        model.addAttribute(PRICE_DTO, response);
        return "redirect:/price/list";
    }
}