package com.ust.pos.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.modelproduct.service.ModelProductService;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.unit.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class
ProductController extends BaseController {
    public static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";

    @Autowired
    CategoryService categoryService;

    @Autowired
    BrandService brandService;

    @Autowired
    UnitService unitService;

    @Autowired
    ModelProductService modelProductService;

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public String home(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("products", productService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "product/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ProductDto productDto, Pageable pageable) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("products", productService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        model.addAttribute("category", categoryService.findBySuperCategoryNotNull());
        model.addAttribute("brand", brandService.findAll(pageable));
        model.addAttribute("unit", unitService.findAll(pageable));
        model.addAttribute("model", modelProductService.findAll(pageable));
        return "product/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto productDto1 = productService.save(productDto);
        if (!productDto1.isSuccess()) {
            model.addAttribute("message", productDto1.getMessage());
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier,Pageable pageable) {
        ProductDto productDto = productService.findByIdentifier(identifier);
        model.addAttribute("category", categoryService.findBySuperCategoryNotNull());
        model.addAttribute("product", productDto);
        model.addAttribute("brand", brandService.findAll(pageable));
        model.addAttribute("unit", unitService.findAll(pageable));
        model.addAttribute("model", modelProductService.findAll(pageable));
        return "product/product";
    }

    @PostMapping("/update")
    public String doupdate(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto productDto1 = productService.update(productDto);
        if (!productDto1.isSuccess()) {
            model.addAttribute("message", productDto1.getMessage());
            model.addAttribute("product", productDto);
            return "product/update";
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        productService.delete(identifier);
        return REDIRECT_PRODUCT_LIST;
    }
}
