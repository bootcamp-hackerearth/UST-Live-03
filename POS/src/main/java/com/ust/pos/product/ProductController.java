package com.ust.pos.product;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.service.ModelService;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.racks.service.RacksService;
import com.ust.pos.shelf.service.ShelfService;
import com.ust.pos.unit.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {

    public static final String PRODUCT_ADD = "product/add";
    public static final String BRANDS = "brands";
    public static final String RACKS = "racks";
    public static final String UNITS = "units";
    public static final String SHELVES = "shelves";
    public static final String MODELS = "models";
    public static final String CATEGORIES = "categories";
    public static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private ShelfService shelfService;

    @Autowired
    private RacksService racksService;

    @GetMapping("/add")
    public String add(Model model, Pageable pageable) {
        model.addAttribute("product", new ProductDto());
        model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
        model.addAttribute(BRANDS, brandService.findAll(pageable));
        model.addAttribute(MODELS, modelService.findAll(pageable));
        model.addAttribute(UNITS, unitService.findAll(pageable));
        model.addAttribute(SHELVES, shelfService.findAllActive());
        model.addAttribute(RACKS, racksService.findAllActive());
        return PRODUCT_ADD;
    }

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("products", productService.findAll(pageable));
        return "product/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier, Pageable pageable) {
        model.addAttribute("product",
                productService.findByIdentifier(identifier));
        model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
        model.addAttribute(BRANDS, brandService.findAll(pageable));
        model.addAttribute(MODELS, modelService.findAll(pageable));
        model.addAttribute(UNITS, unitService.findAll(pageable));
        model.addAttribute(SHELVES, shelfService.findAllActive());
        model.addAttribute(RACKS, racksService.findAllActive());
        return "product/product";
    }

    @PostMapping("/add")
    public String addPost(Model model,
                          @ModelAttribute("product") ProductDto productDto,
                          BindingResult bindingResult, Pageable pageable) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
            model.addAttribute(BRANDS, brandService.findAll(pageable));
            model.addAttribute(MODELS, modelService.findAll(pageable));
            model.addAttribute(UNITS, unitService.findAll(pageable));
            model.addAttribute(SHELVES, shelfService.findAllActive());
            model.addAttribute(RACKS, racksService.findAllActive());
            return PRODUCT_ADD;
        }
        ProductDto response = productService.save(productDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return PRODUCT_ADD;
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @PostMapping("/update")
    public String updatePost(Model model,
                             @ModelAttribute("product") ProductDto productDto, Pageable pageable) {
        ProductDto response = productService.update(productDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
            model.addAttribute(BRANDS, brandService.findAll(pageable));
            model.addAttribute(MODELS, modelService.findAll(pageable));
            model.addAttribute(UNITS, unitService.findAll(pageable));
            model.addAttribute(SHELVES, shelfService.findAllActive());
            model.addAttribute(RACKS, racksService.findAllActive());
            return "product/product";
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        productService.delete(identifier);
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        productService.toggleStatus(identifier);
        return REDIRECT_PRODUCT_LIST;
    }

}

