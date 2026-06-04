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

    public static final String REDIRECT_ROLE_LIST = "redirect:/brand/list";
    public static final String BRANDS = "brands";

    @Autowired
    private BrandService brandService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {

        model.addAttribute(BRANDS, brandService.findAll(pageable));
        return "brand/list";
    }

    @GetMapping("/add")
    public String add(Model model) {

        model.addAttribute("brandDto", new BrandDto());
        return "brand/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute BrandDto brandDto) {

        BrandDto response = brandService.save(brandDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "brand/add";
        }

        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {

        BrandDto response = brandService.findByIdentifier(identifier);
        model.addAttribute("brandDto", response);

        return "brand/brand";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute BrandDto brandDto) {

        BrandDto response = brandService.update(brandDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "brand/brand";
        }

        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {

        brandService.delete(identifier);
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/toggle")
    @ResponseBody
    public String toggle(@RequestParam String identifier) {

        brandService.toggleStatus(identifier);
        return REDIRECT_ROLE_LIST;
    }
}


