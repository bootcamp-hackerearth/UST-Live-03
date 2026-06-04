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
    public static final String BRAND_LIST = "brand/list";
    public static final String MESSAGE = "message";

    @Autowired
    BrandService brandService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(BRANDS, brandService.findAll(pageable));
        return BRAND_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute BrandDto brandDto) {
        return "brand/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute BrandDto brandDto, Pageable pageable) {
        BrandDto response = brandService.save(brandDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Brand Added Successfully");
        }
        model.addAttribute(BRANDS, brandService.findAll(pageable));
        return BRAND_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        BrandDto response = brandService.findByIdentifier(identifier);
        model.addAttribute("brand", response);
        return "brand/brand";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute BrandDto brandDto, Pageable pageable) {
        BrandDto response = brandService.update(brandDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Updated Successfully");
        }
        model.addAttribute(BRANDS, brandService.findAll(pageable));
        return BRAND_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier, Pageable pageable) {
        brandService.delete(identifier);
        model.addAttribute(BRANDS, brandService.findAll(pageable));
        model.addAttribute(MESSAGE, "Brand deleted successfully");
        return BRAND_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        brandService.toggleStatus(identifier);
        return "redirect:/brand/list";
    }
}
