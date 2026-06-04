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
    private static final String REDIRECT_LIST = "redirect:/brand/list";
    @Autowired
    private BrandService brandService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("brands", brandService.findAll(pageable));
        return "brand/list";
    }

    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("brandDto", new BrandDto());
        return "brand/add";
    }

    @PostMapping("/add")
    public String save(@ModelAttribute("brandDto") BrandDto brandDto, Model model) {
        BrandDto response = brandService.save(brandDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            return "brand/add";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/get")
    public String edit(@RequestParam String identifier, Model model) {
        BrandDto brandDto = brandService.findByIdentifier(identifier);
        if (brandDto == null) {
            model.addAttribute(MESSAGE, "Brand not found");
            return REDIRECT_LIST;
        }
        model.addAttribute("brandDto", brandDto);
        return "brand/brand";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("brandDto") BrandDto brandDto, Model model) {
        BrandDto response = brandService.update(brandDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            return "brand/brand";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        brandService.delete(identifier);
        return REDIRECT_LIST;
    }

    @GetMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier) {
        brandService.toggleStatus(identifier);
        return REDIRECT_LIST;
    }
}