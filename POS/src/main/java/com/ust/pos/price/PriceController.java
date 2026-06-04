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

    private static final String VIEW_PRICE_ADD = "price/add";
    private static final String VIEW_PRICE_EDIT = "price/price";
    private static final String REDIRECT_PRICE_LIST = "redirect:/price/list";

    private static final String ATTR_PRICE = "price";
    private static final String ATTR_PRICE_DTO = "priceDto";
    private static final String ATTR_PRODUCTS = "products";
    private static final String ATTR_MESSAGE = "message";
    private static final String ATTR_MESSAGE_TYPE = "messageType";

    private static final String MESSAGE_TYPE_ERROR = "error";

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
    public String add(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute(ATTR_PRICE_DTO, new PriceDto());
        model.addAttribute(ATTR_PRODUCTS, productService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
        return VIEW_PRICE_ADD;
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute PriceDto priceDto, Model model) {
        try {
            priceService.createPrice(priceDto);
        } catch (RuntimeException ex) {
            PaginationDto paginationDto = new PaginationDto();
            model.addAttribute(ATTR_PRODUCTS, productService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
            model.addAttribute(ATTR_MESSAGE, ex.getMessage());
            model.addAttribute(ATTR_MESSAGE_TYPE, MESSAGE_TYPE_ERROR);
            return VIEW_PRICE_ADD;
        }
        return REDIRECT_PRICE_LIST;
    }

    @GetMapping("/get")
    public String update(@RequestParam Long id, Model model) {
        PriceDto price = priceService.getPriceById(id);

        if (!price.isSuccess()) {
            model.addAttribute(ATTR_MESSAGE, price.getMessage());
            model.addAttribute(ATTR_MESSAGE_TYPE, MESSAGE_TYPE_ERROR);
            return REDIRECT_PRICE_LIST;
        }

        model.addAttribute(ATTR_PRICE, price);
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute(ATTR_PRODUCTS, productService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
        return VIEW_PRICE_EDIT;
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute PriceDto priceDto, Model model) {
        try {
            priceService.updatePrice(priceDto);
        } catch (RuntimeException ex) {
            PaginationDto paginationDto = new PaginationDto();
            model.addAttribute(ATTR_PRODUCTS, productService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
            model.addAttribute(ATTR_PRICE, priceDto);
            model.addAttribute(ATTR_MESSAGE, ex.getMessage());
            model.addAttribute(ATTR_MESSAGE_TYPE, MESSAGE_TYPE_ERROR);
            return VIEW_PRICE_EDIT;
        }
        return REDIRECT_PRICE_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam Long id) {
        priceService.deletePrice(id);
        return REDIRECT_PRICE_LIST;
    }
}