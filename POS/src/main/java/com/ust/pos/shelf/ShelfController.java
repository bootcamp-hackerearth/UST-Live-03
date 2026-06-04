package com.ust.pos.shelf;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/shelf")
public class ShelfController {
    public static final String SHELFS = "shelfs";
    public static final String REDIRECT_SHELF_LIST = "redirect:/shelf/list";

    @Autowired
    private ShelfService shelfService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(SHELFS, shelfService.findAll(pageable));
        return "shelf/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ShelfDto shelfDto) {
        return "shelf/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, Pageable pageable, @ModelAttribute ShelfDto shelfDto) {
        ShelfDto response = shelfService.save(shelfDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(SHELFS, shelfService.findAll(pageable));
            return "shelf/add";
        }
        return REDIRECT_SHELF_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, Pageable pageable, @RequestParam String identifier, @ModelAttribute ShelfDto shelfDto) {
        ShelfDto response = shelfService.findByIdentifier(identifier);
        model.addAttribute(SHELFS, shelfService.findAll(pageable));
        model.addAttribute("shelf", response);
        return "shelf/shelf";
    }

    @PostMapping("/update")
    public String updatePost(Model model, Pageable pageable, @ModelAttribute ShelfDto shelfDto) {
        ShelfDto response = shelfService.update(shelfDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(SHELFS, shelfService.findAll(pageable));
        }
        return REDIRECT_SHELF_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        shelfService.delete(identifier);
        return REDIRECT_SHELF_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        shelfService.toggleStatus(identifier);
        return REDIRECT_SHELF_LIST;
    }
}
