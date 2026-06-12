package com.ust.pos.models;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.models.service.ModelsService;
import com.ust.pos.node.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/models")
public class ModelsController {

    public static final String REDIRECT_MODELS_LIST = "redirect:/models/list";
    public static final String NODES = "nodes";
    public static final String MODELS_ADD = "models/add";

    @Autowired
    private ModelsService modelsService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("modelss", modelsService.findAll(pageable));
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return "models/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ModelsDto modelsDto) {
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return MODELS_ADD;
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ModelsDto modelsDto) {
        ModelsDto response = modelsService.save(modelsDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return MODELS_ADD;
        }
        return REDIRECT_MODELS_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ModelsDto response = modelsService.findByIdentifier(identifier);
        model.addAttribute("models", response);
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return "models/models";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ModelsDto userDto) {
        ModelsDto response = modelsService.update(userDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "models/models";
        }
        return REDIRECT_MODELS_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        modelsService.delete(identifier);
        return REDIRECT_MODELS_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        modelsService.toggleStatus(identifier);
        return REDIRECT_MODELS_LIST;
    }
}
