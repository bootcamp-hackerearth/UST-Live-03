package com.ust.pos.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/brand")
public class BrandController extends BaseController {

    public static final String REDIRECT_BRAND_LIST = "redirect:/brand/list";

    @Autowired
    BrandService brandService;

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute BrandDto brandDto) {
        return "brand/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute BrandDto brandDto) {
        BrandDto response = brandService.save(brandDto);
        if (!response.isSuccess()) {
            model.addAttribute("brands", brandService.findAll(null));
            model.addAttribute("message", response.getMessage());
            return "brand/add";
        }
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, @ModelAttribute PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        model.addAttribute("brands", brandService.findAll(pageable));
        return "brand/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        BrandDto response = brandService.findByIdentifier(identifier);
        model.addAttribute("brand", response);
        return "brand/brand";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute BrandDto brandDto) {
        BrandDto response = brandService.update(brandDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        brandService.deleteByIdentifier(identifier);
        return REDIRECT_BRAND_LIST;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public BrandDto toggleStatus(@RequestBody BrandDto brandDto) {
        return brandService.toggleStatus(brandDto.getIdentifier(), brandDto.isStatus());
    }

}