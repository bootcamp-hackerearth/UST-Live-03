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
    public static final String REDIRECT_SHELF_LIST = "redirect:/shelf/list";
    @Autowired
    ShelfService shelfService;

    @GetMapping("/list")
    public String listCategories(Model model, Pageable pageable) {
        model.addAttribute("categories", shelfService.findAll(pageable));
        return "shelf/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, Pageable pageable) {
        model.addAttribute("shelfDto", new ShelfDto());
        model.addAttribute("categories", shelfService.findAll(pageable));
        return "shelf/add";
    }

    @PostMapping("/add")
    public String saveShelf(@ModelAttribute ShelfDto shelfDto) {
        shelfService.save(shelfDto);
        return REDIRECT_SHELF_LIST;
    }


    @GetMapping("/update")
    public String showEditPage(@RequestParam String identifier, Model model) {

        // shelf being edited
        model.addAttribute("shelfDto", shelfService.findByIdentifier(identifier));

        return "shelf/shelf";
    }

    @PostMapping("/update")
    public String saveEditedShelf(@ModelAttribute ShelfDto shelfDto) {

        shelfService.update(shelfDto);

        return REDIRECT_SHELF_LIST;
    }

    @GetMapping("/delete")
    public String deleteShelf(@RequestParam Long id) {
        shelfService.deleteById(id);
        return REDIRECT_SHELF_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status) {
        shelfService.changeShelfStatus(identifier, status);
        return REDIRECT_SHELF_LIST;
    }
}