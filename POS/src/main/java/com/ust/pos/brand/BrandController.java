package com.ust.pos.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/brand")
public class BrandController extends BaseController {

    public static final String REDIRECT_LIST = "redirect:/brand/list";
    public static final String BRAND = "brand";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String SUCCESS_MESSAGE = "successMessage";

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
    public String addPost(@ModelAttribute BrandDto brandDto,
                          RedirectAttributes redirectAttributes) {

        BrandDto response = brandService.save(brandDto);

        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, response.getMessage());
            return "redirect:/brand/add";
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Brand added successfully!");
        return REDIRECT_LIST;
    }

    @GetMapping("/get")
    public String get(Model model,
                      @RequestParam String identifier,
                      RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute(BRAND, brandService.findByIdentifier(identifier));
            return "brand/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Brand not found!");
            return REDIRECT_LIST;
        }
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BrandDto brandDto,
                         RedirectAttributes redirectAttributes) {

        BrandDto response = brandService.update(brandDto);

        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, response.getMessage());
            return "redirect:/brand/get?identifier=" + brandDto.getIdentifier();
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Brand updated successfully!");
        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier,
                         RedirectAttributes redirectAttributes) {
        try {
            brandService.delete(identifier);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Brand deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Failed to delete brand!");
        }
        return REDIRECT_LIST;
    }
}
