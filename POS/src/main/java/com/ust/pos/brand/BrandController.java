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
    private static final String REDIRECT_LIST = "redirect:/brand/list";
    @Autowired
    BrandService brandService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("brands", brandService.findAll());
        return "brand/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute BrandDto brandDto) {
        model.addAttribute("brands", brandService.findAll());
        return "brand/add";
    }

    @PostMapping("/add")
    public String addData(Model model, @ModelAttribute BrandDto brandDto) {
        BrandDto response = brandService.save(brandDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "brand/add";
        }
        return REDIRECT_LIST;
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
            return "/brand/brand";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        brandService.delete(identifier);
        return REDIRECT_LIST;
    }
}