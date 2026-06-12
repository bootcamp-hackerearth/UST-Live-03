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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/price")
public class PriceController extends BaseController {

    private static final String PRICE_LIST = "price/list";
    private static final String PRICE_ADD = "price/add";
    private static final String PRICE_VIEW = "price/price";
    private static final String REDIRECT_PRICE_ADD = "redirect:/price/add";
    private static final String REDIRECT_PRICE_LIST = "redirect:/price/list";
    private static final String MESSAGE = "message";

    @Autowired
    private PriceService priceService;

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public String list(Model model, @ModelAttribute PaginationDto paginationDto) {
        model.addAttribute("prices", priceService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return PRICE_LIST;
    }

    @GetMapping("/add")
    public String add(@ModelAttribute PriceDto priceDto, Model model) {
        model.addAttribute("products", productService.findByStatusTrue());
        return PRICE_ADD;
    }

    @PostMapping("/add")
    public String addPost(RedirectAttributes redirectAttributes, @ModelAttribute PriceDto priceDto) {
        PriceDto priceDto1 = priceService.save(priceDto);
        redirectAttributes.addFlashAttribute(MESSAGE, priceDto1.getMessage());
        if (!priceDto1.isSuccess()) {
            return REDIRECT_PRICE_ADD;
        } else {
            return REDIRECT_PRICE_LIST;
        }
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam long id) {
        model.addAttribute("products", productService.findAll(null));
        model.addAttribute("priceDto", priceService.findById(id));
        return PRICE_VIEW;
    }

    @PostMapping("/update")
    public String updatePost(RedirectAttributes redirectAttributes, @ModelAttribute PriceDto priceDto) {
        priceService.update(priceDto);
        redirectAttributes.addFlashAttribute(MESSAGE, "Updated successfully");
        return REDIRECT_PRICE_LIST;
    }

    @GetMapping("/delete")
    public String delete(RedirectAttributes redirectAttributes, @RequestParam String identifier) {
        priceService.delete(identifier);
        redirectAttributes.addFlashAttribute(MESSAGE, "Delete success");

        return REDIRECT_PRICE_LIST;
    }
}
