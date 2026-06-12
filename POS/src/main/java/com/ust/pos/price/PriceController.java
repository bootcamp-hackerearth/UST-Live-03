package com.ust.pos.price;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
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
    public static final String PRODUCTS = "products";

    @Autowired
    PriceService priceService;
    @Autowired
    ProductService productService;

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute PriceDto priceDto) {
        model.addAttribute(PRODUCTS, productService.findAll(null));
        return "price/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute PriceDto priceDto) {
        PriceDto response = priceService.save(priceDto);
        if (!response.isSuccess()) {
            model.addAttribute("prices", priceService.findAll(null));
            model.addAttribute(PRODUCTS, productService.findAll(null));
            model.addAttribute("message", response.getMessage());
            return "price/add";
        }
        return REDIRECT_PRICE_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, @ModelAttribute PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        model.addAttribute("prices", priceService.findAll(pageable));
        return "price/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        PriceDto response = priceService.findByIdentifier(identifier);
        model.addAttribute("price", response);
        model.addAttribute(PRODUCTS, productService.findAll(null));
        return "price/price";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute PriceDto priceDto) {
        PriceDto response = priceService.update(priceDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_PRICE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        priceService.deleteByIdentifier(identifier);
        return REDIRECT_PRICE_LIST;
    }

}