package com.ust.pos.product;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.models.service.ModelsService;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.unit.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/product")
public class ProductController {
    public static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";
    public static final String NODES = "nodes";
    public static final String STOCK_ADD = "product/add";

    @Autowired
    private ProductService productService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ModelsService modelsService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("products", productService.findAll(pageable));
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return "product/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ProductDto productDto) {
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        model.addAttribute("units", unitService.findAllActive());
        model.addAttribute("brands", brandService.findAllActive());
        model.addAttribute("models", modelsService.findAllActive());
        model.addAttribute("categorys", categoryService.findAllWithSuperCategoryEmpty());
        return STOCK_ADD;
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto response = productService.save(productDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return STOCK_ADD;
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ProductDto response = productService.findByIdentifier(identifier);
        model.addAttribute("product", response);
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        model.addAttribute("units", unitService.findAllActive());
        model.addAttribute("brands", brandService.findAllActive());
        model.addAttribute("models", modelsService.findAllActive());
        model.addAttribute("categorys", categoryService.findAllWithSuperCategoryEmpty());
        return "product/product";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto response = productService.update(productDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "product/product";
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        productService.delete(identifier);
        return REDIRECT_PRODUCT_LIST;
    }
}
