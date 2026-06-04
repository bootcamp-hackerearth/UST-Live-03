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

    public static final String MESSAGE = "message";
    public static final String BRAND = "brand";
    public static final String REDIRECT_BRAND_LIST = "redirect:/brand/list";

    @Autowired
    private BrandService brandService;

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(BRAND, new BrandDto());
        return "brand/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute(BRAND) BrandDto brandDto) {
        BrandDto response = brandService.save(brandDto);

        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute(BRAND, brandDto);
            return "brand/add";
        }
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("brands", brandService.findAll(pageable));
        return "brand/list";
    }

    @GetMapping("/get")
    public String get(Model model, @RequestParam("identifier") String identifier) {
        BrandDto brandDto = brandService.findByIdentifier(identifier);

        if (brandDto == null) {
            model.addAttribute(MESSAGE, "Brand not found");
            return REDIRECT_BRAND_LIST;
        }
        model.addAttribute(BRAND, brandDto);
        return "brand/brand";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute(BRAND) BrandDto brandDto) {
        BrandDto response = brandService.update(brandDto);

        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute(BRAND, brandDto);
            return "brand/brand";
        }
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("identifier") String identifier) {
        brandService.deleteByIdentifier(identifier);
        return REDIRECT_BRAND_LIST;
    }
}