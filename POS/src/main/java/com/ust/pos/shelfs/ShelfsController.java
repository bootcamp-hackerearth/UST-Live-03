package com.ust.pos.shelfs;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.shelfs.service.ShelfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/shelfs")
public class ShelfsController {

    public static final String REDIRECT_SHELFS_LIST = "redirect:/shelfs/list";
    public static final String NODES = "nodes";
    public static final String STOCK_ADD = "shelfs/add";

    @Autowired
    private ShelfsService shelfsService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("shelfss", shelfsService.findAll(pageable));
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return "shelfs/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ShelfsDto shelfsDto) {
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        model.addAttribute("categorys", categoryService.findAllWithSuperCategoryEmpty());
        return STOCK_ADD;
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ShelfsDto shelfsDto) {
        ShelfsDto response = shelfsService.save(shelfsDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(NODES, nodeService.getNodesForRoles());
            return STOCK_ADD;
        }
        return REDIRECT_SHELFS_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ShelfsDto response = shelfsService.findByIdentifier(identifier);
        model.addAttribute("shelfs", response);
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        model.addAttribute("categorys", categoryService.findAllWithSuperCategoryEmpty());
        return "shelfs/shelfs";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ShelfsDto shelfsDto) {
        ShelfsDto response = shelfsService.update(shelfsDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "shelfs/shelfs";
        }
        return REDIRECT_SHELFS_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        shelfsService.delete(identifier);
        return REDIRECT_SHELFS_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        shelfsService.toggleStatus(identifier);
        return REDIRECT_SHELFS_LIST;
    }
}
