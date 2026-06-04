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
public class BrandController {
    public static final String BRANDS = "brands";
    public static final String REDIRECT_BRAND_LIST = "redirect:/brand/list";

    @Autowired
    private BrandService brandService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(BRANDS, brandService.findAll(pageable));
        return "brand/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute BrandDto brandDto) {
        return "brand/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, Pageable pageable, @ModelAttribute BrandDto brandDto) {
        BrandDto response = brandService.save(brandDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(BRANDS, brandService.findAll(pageable));
            return "brand/add";
        }
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, Pageable pageable, @RequestParam String identifier, @ModelAttribute BrandDto brandDto) {
        BrandDto response = brandService.findByIdentifier(identifier);
        model.addAttribute(BRANDS, brandService.findAll(pageable));
        model.addAttribute("brand", response);
        return "brand/brand";
    }

    @PostMapping("/update")
    public String updatePost(Model model, Pageable pageable, @ModelAttribute BrandDto brandDto) {
        BrandDto response = brandService.update(brandDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("brand", brandService.findAll(pageable));
        }
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        brandService.delete(identifier);
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        brandService.toggleStatus(identifier);
        return REDIRECT_BRAND_LIST;
    }
}
