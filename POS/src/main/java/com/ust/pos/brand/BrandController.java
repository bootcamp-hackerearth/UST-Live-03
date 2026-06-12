package com.ust.pos.brand;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/brand")
public class BrandController extends BaseController {

    public static final String REDIRECT_BRAND_LIST = "redirect:/brand/list";

    @Autowired
    BrandService brandService;

    @GetMapping("/list")
    public String home(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("brands", brandService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "brand/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute BrandDto userDto) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("brand", brandService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "brand/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute BrandDto userDto) {
        BrandDto response = brandService.save(userDto);
        PaginationDto paginationDto = new PaginationDto();
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("brand", brandService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                    paginationDto.getSortDirection(), paginationDto.getSortField())));
            return "brand/add";
        }
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        BrandDto brandDto = brandService.findByIdentifier(identifier);
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("categories", brandService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
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

    @PostMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier) {
        brandService.toggleStatus(identifier);
        return REDIRECT_BRAND_LIST;
    }
}