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

    public static final String REDIRECT_PRICE_LIST = "redirect:/price/list";

    @Autowired
    private PriceService priceService;

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public String home(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("prices", priceService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "price/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute PriceDto priceDto) {
        model.addAttribute("products", productService.findIfTrue());
        return "price/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute PriceDto priceDto) {
        PriceDto response = priceService.save(priceDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "price/add";
        }
        return REDIRECT_PRICE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        PriceDto response = priceService.findByIdentifier(identifier);
        model.addAttribute("price", response);
        return "price/price";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute PriceDto priceDto) {
        PriceDto response = priceService.update(priceDto);
        if (!response.isSuccess()) {
            model.addAttribute("price", response);
            model.addAttribute("message", response.getMessage());
            return "price/price";
        }
        return REDIRECT_PRICE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        priceService.delete(identifier);
        return REDIRECT_PRICE_LIST;
    }

    @PostMapping("/toggle-status")
    @ResponseBody
    public void toggle(Model model, @RequestParam String identifier) {
        priceService.toggleStatus(identifier);
    }
}
