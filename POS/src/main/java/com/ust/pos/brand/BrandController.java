package com.ust.pos.brand;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/brand")
public class BrandController{
    public static final String BRANDS = "brands";
    public static final String REDIRECT_BRAND_LIST = "redirect:/brand/list";
    @Autowired
    BrandService brandService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(BRANDS, brandService.findAll(pageable));
        return "brand/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute BrandDto brandDto,Pageable pageable) {
        model.addAttribute(BRANDS, brandService.findAll(pageable));
        return "brand/add";
    }

    @PostMapping("/add")
    public String addproduct(Model model, @ModelAttribute BrandDto brandDto) {
        brandService.save(brandDto);
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        brandService.delete(identifier);
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier,Pageable pageable) {
        BrandDto response = brandService.findByIdentifier(identifier);
        model.addAttribute(BRANDS, brandService.findAll(pageable));
        model.addAttribute(BRANDS, response);
        return "brand/brand";
    }

    @PostMapping("/update")
    public String updateCategory(Model model, @ModelAttribute BrandDto brandDto) {
        BrandDto response = brandService.update(brandDto);
        model.addAttribute(BRANDS, response);

        if (!response.isSuccess()) {
            model.addAttribute(BRANDS, response);
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status) {
        brandService.changeToggleStatus(identifier, status);
        return REDIRECT_BRAND_LIST;
    }
}
