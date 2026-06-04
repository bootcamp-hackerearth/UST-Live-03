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

    public static final String SHELVES = "shelves";
    public static final String SHELF_LIST = "shelf/list";
    public static final String MESSAGE = "message";

    @Autowired
    ShelfService shelfService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(SHELVES, shelfService.findAll(pageable));
        return SHELF_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ShelfDto shelfDto) {
        return "shelf/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ShelfDto shelfDto, Pageable pageable) {
        ShelfDto response = shelfService.save(shelfDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Shelf Added Successfully");
        }
        model.addAttribute(SHELVES, shelfService.findAll(pageable));
        return SHELF_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ShelfDto response = shelfService.findByIdentifier(identifier);
        model.addAttribute("shelf", response);
        return "shelf/shelf";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ShelfDto shelfDto, Pageable pageable) {
        ShelfDto response = shelfService.update(shelfDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Updated Successfully");
        }
        model.addAttribute(SHELVES, shelfService.findAll(pageable));
        return SHELF_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier, Pageable pageable) {
        shelfService.delete(identifier);
        model.addAttribute(SHELVES, shelfService.findAll(pageable));
        model.addAttribute(MESSAGE, "Shelf deleted successfully");
        return SHELF_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        shelfService.toggleStatus(identifier);
        return "redirect:/shelf/list";
    }
}
