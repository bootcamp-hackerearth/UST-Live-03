package com.ust.pos.price;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.node.service.NodeService;
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

    public static final String REDIRECT_STOCK_LIST = "redirect:/price/list";
    public static final String NODES = "nodes";
    public static final String STOCK_ADD = "price/add";
    public static final String PRODUCTS = "products";

    @Autowired
    private PriceService priceService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("prices", priceService.findAll(pageable));
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return "price/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute PriceDto priceDto, Pageable pageable) {
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        WsDto<ProductDto> productDtos = productService.findAll(pageable);
        model.addAttribute(PRODUCTS, productDtos);
        return STOCK_ADD;
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute PriceDto priceDto, Pageable pageable) {
        PriceDto response = priceService.save(priceDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            WsDto<ProductDto> productDtos = productService.findAll(pageable);
            model.addAttribute(PRODUCTS, productDtos);
            return STOCK_ADD;
        }
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier, Pageable pageable) {
        PriceDto response = priceService.findByIdentifier(identifier);
        model.addAttribute("price", response);
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        WsDto<ProductDto> productDtos = productService.findAll(pageable);
        model.addAttribute(PRODUCTS, productDtos);
        return "price/price";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute PriceDto userDto, Pageable pageable) {
        PriceDto response = priceService.update(userDto);
        if (!response.isSuccess()) {
            model.addAttribute(NODES, nodeService.getNodesForRoles());
            model.addAttribute("message", response.getMessage());
            WsDto<ProductDto> productDtos = productService.findAll(pageable);
            model.addAttribute(PRODUCTS, productDtos);
            return "price/price";
        }
        return REDIRECT_STOCK_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        priceService.delete(identifier);
        return REDIRECT_STOCK_LIST;
    }
}
