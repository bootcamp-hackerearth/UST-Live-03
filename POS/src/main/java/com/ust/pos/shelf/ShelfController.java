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

    public static final String REDIRECT_LIST = "redirect:/shelf/list";
    public static final String SHELF = "shelf";
    public static final String SHELVES = "shelves";
    public static final String MESSAGE = "message";

    @Autowired
    private ShelfService shelfService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute(SHELVES, shelfService.findAll(pageable));
        return "shelf/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(SHELF, new ShelfDto());
        return "shelf/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ShelfDto shelfDto) {

        if (shelfDto.getStatus() == null) {
            shelfDto.setStatus(true);
        }

        ShelfDto response = shelfService.save(shelfDto);

        if (!response.isSuccess()) {
            model.addAttribute(SHELF, shelfDto);
            model.addAttribute(MESSAGE, response.getMessage());
            return "shelf/add";
        }

        return REDIRECT_LIST;
    }

    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {

        model.addAttribute(SHELF, shelfService.findByIdentifier(identifier));
        return "shelf/shelf";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute ShelfDto shelfDto) {

        ShelfDto response = shelfService.update(shelfDto);

        if (!response.isSuccess()) {
            model.addAttribute(SHELF, shelfDto);
            model.addAttribute(MESSAGE, response.getMessage());
            return "shelf/shelf";
        }

        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        shelfService.delete(identifier);
        return REDIRECT_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        shelfService.toggleStatus(identifier);
        return REDIRECT_LIST;
    }
}