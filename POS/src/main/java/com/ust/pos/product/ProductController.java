package com.ust.pos.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.models.service.ModelService;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.unit.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/product")
public class ProductController extends BaseController {

    private static final String PRODUCT_LIST = "product/list";
    private static final String PRODUCT_ADD = "product/add";
    private static final String PRODUCT_VIEW = "product/product";
    private static final String REDIRECT_PRODUCT_ADD = "redirect:/product/add";
    private static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";
    private static final String MESSAGE = "message";

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private UnitService unitService;

    @GetMapping("/list")
    public String list(Model model, @ModelAttribute PaginationDto paginationDto) {
        model.addAttribute("products", productService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return PRODUCT_LIST;
    }

    @GetMapping("/add")
    public String add(@ModelAttribute ProductDto productDto, Model model) {
        model.addAttribute("brands", brandService.findByStatusTrue());
        model.addAttribute("models", modelService.findByStatusTrue());
        model.addAttribute("units", unitService.findAll(null));
        model.addAttribute("categories", categoryService.findBySuperCategoryIsNotNullAndStatusTrue());
        return PRODUCT_ADD;
    }

    @PostMapping("/add")
    public String addPost(RedirectAttributes redirectAttributes, @ModelAttribute ProductDto productDto) {
        ProductDto productDto1 = productService.save(productDto);
        redirectAttributes.addFlashAttribute(MESSAGE, productDto1.getMessage());
        if (!productDto1.isSuccess()) {
            return REDIRECT_PRODUCT_ADD;
        } else {
            return REDIRECT_PRODUCT_LIST;
        }
    }

    @PostMapping("/toggle")
    @ResponseBody
    public ProductDto toggleStatus(@RequestBody ProductDto dto) {
        return productService.updateStatus(dto.getIdentifier(), dto.isStatus());
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        model.addAttribute("brands", brandService.findByStatusTrue());
        model.addAttribute("models", modelService.findByStatusTrue());
        model.addAttribute("units", unitService.findAll(null));
        model.addAttribute("categories", categoryService.findBySuperCategoryIsNotNullAndStatusTrue());
        model.addAttribute("productDto", productService.findByIdentifier(identifier));
        return PRODUCT_VIEW;
    }

    @PostMapping("/update")
    public String updatePost(RedirectAttributes redirectAttributes, @ModelAttribute ProductDto productDto) {
        ProductDto productDto1 = productService.update(productDto);
        redirectAttributes.addFlashAttribute(MESSAGE, productDto1.getMessage());
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/delete")
    public String delete(RedirectAttributes redirectAttributes, @RequestParam String identifier) {
        productService.delete(identifier);
        redirectAttributes.addFlashAttribute(MESSAGE, "Delete success");

        return REDIRECT_PRODUCT_LIST;
    }
}
