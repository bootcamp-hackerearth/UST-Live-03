package com.ust.pos.brand;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.node.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/brand")
public class BrandController {

    public static final String REDIRECT_BRAND_LIST = "redirect:/brand/list";
    public static final String NODES = "nodes";
    public static final String BRAND_ADD = "brand/add";

    @Autowired
    private BrandService brandService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("brands", brandService.findAll(pageable));
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return "brand/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute BrandDto brandDto) {
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return BRAND_ADD;
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute BrandDto brandDto) {
        BrandDto response = brandService.save(brandDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return BRAND_ADD;
        }
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        BrandDto response = brandService.findByIdentifier(identifier);
        model.addAttribute("brand", response);
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return "brand/brand";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute BrandDto userDto) {
        BrandDto response = brandService.update(userDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "brand/brand";
        }
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        brandService.delete(identifier);
        return REDIRECT_BRAND_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        brandService.toggleStatus(identifier);
        return REDIRECT_BRAND_LIST;
    }
}

