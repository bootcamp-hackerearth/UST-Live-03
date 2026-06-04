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

    public static final String SHELF = "shelf";
    public static final String REDIRECT_SHELF_LIST = "redirect:/shelf/list";

    @Autowired
    private ShelfService shelfService;

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(SHELF, new ShelfDto());
        return "shelf/add";
    }

    @PostMapping("/add")
    public String addPost(Model model,
                          @ModelAttribute(SHELF) ShelfDto shelfDto) {
        ShelfDto response = shelfService.save(shelfDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(SHELF, shelfDto);
            return "shelf/add";
        }
        return REDIRECT_SHELF_LIST;
    }

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("shelfs", shelfService.findAll(pageable));
        return "shelf/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ShelfDto response = shelfService.findByIdentifier(identifier);
        model.addAttribute(SHELF, response);
        return "shelf/shelf";
    }

    @PostMapping("/update")
    public String updatePost(Model model,
                             @ModelAttribute(SHELF) ShelfDto shelfDto) {
        ShelfDto response = shelfService.update(shelfDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "shelf/shelf";
        }
        return REDIRECT_SHELF_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        shelfService.delete(identifier);
        return REDIRECT_SHELF_LIST;
    }

    @ModelAttribute(SHELF)
    public ShelfDto shelfFallback() {
        return new ShelfDto();
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        shelfService.toggleStatus(identifier);
        return REDIRECT_SHELF_LIST;
    }

}