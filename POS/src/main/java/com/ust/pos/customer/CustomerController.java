package com.ust.pos.customer;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.node.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    public static final String REDIRECT_CUSTOMER_LIST = "redirect:/customer/list";
    public static final String NODES = "nodes";
    public static final String CUSTOMER_ADD = "customer/add";

    @Autowired
    private CustomerService customerService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("customers", customerService.findAll(pageable));
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return "customer/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute CustomerDto customerDto) {
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return CUSTOMER_ADD;
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute CustomerDto customerDto) {
        CustomerDto response = customerService.save(customerDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return CUSTOMER_ADD;
        }
        return REDIRECT_CUSTOMER_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        CustomerDto response = customerService.findByIdentifierWithAddressDto(identifier);
        model.addAttribute("customer", response);
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return "customer/customer";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute CustomerDto userDto) {
        CustomerDto response = customerService.update(userDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "customer/customer";
        }
        return REDIRECT_CUSTOMER_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        customerService.delete(identifier);
        return REDIRECT_CUSTOMER_LIST;
    }
}
