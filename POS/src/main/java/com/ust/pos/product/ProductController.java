package com.ust.pos.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.unit.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController extends BaseController {

    public static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";

    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    BrandService brandService;
    @Autowired
    UnitService unitService;

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ProductDto productDto) {
        model.addAttribute("categories", categoryService.findSubCategories());
        model.addAttribute("brands", brandService.findAll(null));
        model.addAttribute("units", unitService.findAll(null));
        return "product/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto response = productService.save(productDto);
        if (!response.isSuccess()) {
            model.addAttribute("products", productService.findAll(null));
            model.addAttribute("message", response.getMessage());
            return "product/add";
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, @ModelAttribute PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        model.addAttribute("products", productService.findAll(pageable));
        return "product/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ProductDto response = productService.findByIdentifier(identifier);
        model.addAttribute("categories", categoryService.findSubCategories());
        model.addAttribute("product", response);
        model.addAttribute("brands", brandService.findAll(null));
        model.addAttribute("units", unitService.findAll(null));
        return "product/product";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto response = productService.update(productDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        productService.deleteByIdentifier(identifier);
        return REDIRECT_PRODUCT_LIST;
    }

}