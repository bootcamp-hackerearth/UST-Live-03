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

    private static final String BRAND_LIST = "brand/list";
    private static final String BRAND_ADD = "brand/add";
    private static final String BRAND_VIEW = "brand/brand";
    private static final String REDIRECT_BRAND_LIST = "redirect:/brand/list";

    @Autowired
    private BrandService brandService;

    @GetMapping("/list")
    public String list(Model model, @ModelAttribute PaginationDto paginationDto) {
        model.addAttribute("brands", brandService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return BRAND_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute BrandDto brandDto) {
        return BRAND_ADD;
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute BrandDto brandDto) {
        brandService.save(brandDto);
        return REDIRECT_BRAND_LIST;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public BrandDto toggleStatus(@RequestBody BrandDto dto) {
        return brandService.updateStatus(dto.getIdentifier(), dto.isStatus());
    }

    @GetMapping("/get")
    public String update(@RequestParam String identifier, Model model) {
        model.addAttribute("brandDto", brandService.findByIdentifier(identifier));
        return BRAND_VIEW;
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute BrandDto brandDto) {
        brandService.update(brandDto);
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        brandService.delete(identifier);
        return REDIRECT_BRAND_LIST;
    }
}
