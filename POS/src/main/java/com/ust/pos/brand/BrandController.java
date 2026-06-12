package com.ust.pos.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/brand")
public class BrandController extends BaseController {

    public static final String REDIRECT_LIST = "redirect:/brand/list";
    public static final String BRAND = "brand";

    @Autowired
    private BrandService brandService;


    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("brands", brandService.findAll(pageable));
        return "brand/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(BRAND, new BrandDto());
        return "brand/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute BrandDto dto) {
        BrandDto response = brandService.save(dto);

        if (!response.isSuccess()) {
            model.addAttribute(BRAND, dto);
            model.addAttribute("message", response.getMessage());
            return "brand/add";
        }

        return REDIRECT_LIST;
    }

    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        model.addAttribute(BRAND, brandService.findByIdentifier(identifier));
        return "brand/edit";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute BrandDto dto) {
        BrandDto response = brandService.update(dto);

        if (!response.isSuccess()) {
            model.addAttribute(BRAND, dto);
            model.addAttribute("message", response.getMessage());
            return "brand/edit";
        }

        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        brandService.delete(identifier);
        return REDIRECT_LIST;
    }
}