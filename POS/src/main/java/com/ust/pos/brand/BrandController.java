package com.ust.pos.brand;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/brand")
public class BrandController {

    public static final String REDIRECT_BRAND_LIST = "redirect:/brand/list";

    @Autowired
    private BrandService brandService;

    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute("brands", brandService.findAll());
        return "brand/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute BrandDto brandDto) {
        model.addAttribute("brand", brandService.findAll());
        return "brand/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute BrandDto brandDto) {
        BrandDto response = brandService.save(brandDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("brand", brandService.findAll());
            return "brand/add";
        }
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        BrandDto brandDto = brandService.findByIdentifier(identifier);
        model.addAttribute("categories", brandService.findAll());
        model.addAttribute("brandDto", brandDto);
        return "brand/brand";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute BrandDto brandDto) {
        BrandDto response = brandService.update(brandDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("brandDto", brandDto);
            return "brand/brand";
        }
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        brandService.delete(identifier);
        return REDIRECT_BRAND_LIST;
    }
}