package com.ust.pos.price;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/price")
public class PriceController extends BaseController {
    public static final String REDIRECT_LIST = "redirect:/price/list";
    @Autowired
    ProductService productService;
    @Autowired
    private PriceService priceService;

    @GetMapping("/list")
    public String list(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("prices", priceService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "price/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("priceDto", new PriceDto());
        model.addAttribute("product", productService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "price/add";
    }

    @PostMapping("/add")
    public String save(Model model, @ModelAttribute PriceDto priceDto) {
        PriceDto response = priceService.save(priceDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "price/add";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        model.addAttribute("priceDto", priceService.findByIdentifier(identifier));
        return "price/price";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute PriceDto priceDto) {
        PriceDto response = priceService.update(priceDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "price/price";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        priceService.delete(identifier);
        return REDIRECT_LIST;
    }
}

