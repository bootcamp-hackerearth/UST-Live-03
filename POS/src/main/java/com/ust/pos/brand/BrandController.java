package com.ust.pos.brand;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/brand")
public class BrandController {
    public static final String REDIRECT_BRAND_LIST = "redirect:/brand/list";
    public static final String MESSAGE = "message";
    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductService productService;

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/add")
    public String add() {
        return "brand/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute BrandDto brandDto) {
        BrandDto response = brandService.save(brandDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            return "redirect:/brand/add";
        }
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("brands", brandService.findAll(pageable));
        return "brand/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        BrandDto response = brandService.findByIdentifier(identifier);
        model.addAttribute("brand", response);
        return "brand/brand";
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute BrandDto brandDto) {
        BrandDto response = brandService.update(brandDto);
        if (!response.isSuccess()) {
            return REDIRECT_BRAND_LIST;
        }
        return "redirect:/brand/get?identifier=" + brandDto.getIdentifier();
    }

    @PostMapping("/toggle")
    @ResponseBody
    public void toggleStatus(@RequestParam String identifier) {
        brandService.toggleStatus(identifier);
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        brandService.delete(identifier);
        return REDIRECT_BRAND_LIST;
    }
}
