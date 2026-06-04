package com.ust.pos.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.models.service.ModelsService;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.unit.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController extends BaseController {

    public static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";
    public static final String MODEL = "model";
    public static final String BRAND = "brand";
    public static final String CATEGORIES = "categories";

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private ModelsService modelsService;

    @GetMapping("/list")
    public String home(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("products", productService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "product/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ProductDto productDto) {

        model.addAttribute("productDto", productDto);
        model.addAttribute(CATEGORIES, categoryService.findIfTrue());
        model.addAttribute(BRAND, brandService.findIfTrue());
        model.addAttribute("unit", unitService.findIfTrue());
        model.addAttribute(MODEL, modelsService.findIfTrue());

        return "product/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ProductDto productDto) {

        ProductDto response = productService.save(productDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("productDto", productDto);
            PaginationDto paginationDto = new PaginationDto();
            model.addAttribute(CATEGORIES, categoryService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
            model.addAttribute(BRAND, brandService.findIfTrue());
            model.addAttribute("unit", unitService.findIfTrue());
            model.addAttribute(MODEL, modelsService.findIfTrue());
            return "product/add";
        }

        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/get/{identifier}")
    public String update(Model model, @PathVariable String identifier) {

        ProductDto response = productService.findByIdentifier(identifier);

        model.addAttribute("product", response);
        model.addAttribute(CATEGORIES, categoryService.findIfTrue());
        model.addAttribute(BRAND, brandService.findIfTrue());
        model.addAttribute("unit", unitService.findIfTrue());
        model.addAttribute(MODEL, modelsService.findIfTrue());

        return "product/product";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ProductDto productDto) {

        ProductDto response = productService.update(productDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("product", response);
            model.addAttribute(CATEGORIES, categoryService.findIfTrue());
            model.addAttribute(BRAND, brandService.findIfTrue());
            model.addAttribute("unit", unitService.findIfTrue());
            model.addAttribute(MODEL, modelsService.findIfTrue());
            return "product/product";
        }

        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/delete/{identifier}")
    public String delete(@PathVariable String identifier) {
        productService.delete(identifier);
        return REDIRECT_PRODUCT_LIST;
    }

    @PostMapping("/toggle-status")
    @ResponseBody
    public void toggleStatus(@RequestParam String identifier) {
        productService.toggleStatus(identifier);
    }
}