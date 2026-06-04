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
    public static final String REDIRECT_BRAND_LIST = "redirect:/brand/list";
    public static final String BRANDS = "brands";

    @Autowired
    BrandService brandService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute(BRANDS, brandService.findAll(pageable));
        return "brand/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("brandDto", new BrandDto());
        return "brand/add";
    }

    @PostMapping("/add")
    public String saveBrand(@ModelAttribute BrandDto brandDto) {
        brandService.save(brandDto);
        return REDIRECT_BRAND_LIST;
    }


    @GetMapping("/update")
    public String showEditPage(@RequestParam String identifier, Model model) {

        model.addAttribute("brandDto", brandService.findByIdentifier(identifier));
        return "brand/brand";
    }

    @PostMapping("/update")
    public String saveEditedBrand(@ModelAttribute BrandDto brandDto) {

        brandService.update(brandDto);
        return REDIRECT_BRAND_LIST;
    }


    @GetMapping("/delete")
    public String deleteBrand(@RequestParam Long id) {

        brandService.deleteById(id);
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status) {
        brandService.changeBrandStatus(identifier, status);
        return REDIRECT_BRAND_LIST;
    }
}