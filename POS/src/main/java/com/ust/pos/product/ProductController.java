package com.ust.pos.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.rack.service.RackService;
import com.ust.pos.shelf.service.ShelfService;
import com.ust.pos.unit.service.UnitService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController extends BaseController {

    public static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";
    public static final String SHELFS = "shelfs";
    public static final String RACKS = "racks";

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ShelfService shelfService;
    private final RackService rackService;
    private final UnitService unitService;

    public ProductController(
            ProductService productService,
            CategoryService categoryService,
            BrandService brandService,
            ShelfService shelfService,
            RackService rackService,
            UnitService unitService) {

        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.shelfService = shelfService;
        this.rackService = rackService;
        this.unitService = unitService;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ProductDto productDto,Pageable pageable) {
        model.addAttribute("categories", categoryService.findSubCategories());
        model.addAttribute("brands", brandService.findAll(pageable));
        model.addAttribute(SHELFS, shelfService.findActiveShelves());
        model.addAttribute(RACKS, rackService.findActiveRacks());
        model.addAttribute("units", unitService.findAll(pageable));
        return "product/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ProductDto productDto,Pageable pageable) {
        ProductDto response = productService.save(productDto);
        if (!response.isSuccess()) {
            model.addAttribute("products", productService.findAll(pageable));
            model.addAttribute("message", response.getMessage());
            return "product/add";
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, @ModelAttribute PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),paginationDto.getSortField());
        model.addAttribute("products", productService.findAll(pageable));
        model.addAttribute(SHELFS, shelfService.findActiveShelves());
        model.addAttribute(RACKS, rackService.findActiveRacks());
        return "product/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier,Pageable pageable) {
        ProductDto response = productService.findByIdentifier(identifier);
        model.addAttribute("categories", categoryService.findSubCategories());
        model.addAttribute("product", response);
        model.addAttribute("brands", brandService.findAll(pageable));
        model.addAttribute(SHELFS, shelfService.findActiveShelves());
        model.addAttribute(RACKS, rackService.findActiveRacks());
        model.addAttribute("units", unitService.findAll(pageable));
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

    @PostMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier, boolean status) {
        productService.toggleStatus(identifier, status);
        return REDIRECT_PRODUCT_LIST;
    }
}