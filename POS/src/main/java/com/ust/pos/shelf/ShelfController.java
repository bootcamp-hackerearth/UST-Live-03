package com.ust.pos.shelf;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/shelf")
public class ShelfController {

    public static final String REDIRECT_SHELF_LIST = "redirect:/shelf/list";

    @Autowired
    private ShelfService shelfService;

    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute("shelfs", shelfService.findAll(null));
        return "shelf/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ShelfDto shelfDto) {
        model.addAttribute("shelf", shelfService.findAll());
        return "shelf/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ShelfDto shelfDto) {
        ShelfDto response = shelfService.save(shelfDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("shelf", shelfService.findAll());
            return "shelf/add";
        }
        return REDIRECT_SHELF_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ShelfDto shelfDto = shelfService.findByIdentifier(identifier);
        model.addAttribute("categories", shelfService.findAll());
        model.addAttribute("shelfDto", shelfDto);
        return "shelf/shelf";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ShelfDto shelfDto) {
        ShelfDto response = shelfService.update(shelfDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("shelfDto", shelfDto);
            return "shelf/shelf";
        }
        return REDIRECT_SHELF_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        shelfService.delete(identifier);
        return REDIRECT_SHELF_LIST;
    }

    @PostMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier) {
        shelfService.toggleStatus(identifier);
        return REDIRECT_SHELF_LIST;
    }
}