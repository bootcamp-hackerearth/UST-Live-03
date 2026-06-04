package com.ust.pos.shelf;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/shelf")
public class ShelfController {

    public static final String REDIRECT_LIST = "redirect:/shelf/list";
    public static final String SHELF = "shelf";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String ERROR_MESSAGE = "errorMessage";

    @Autowired
    private ShelfService shelfService;


    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("shelves", shelfService.findAll(pageable));
        return "shelf/list";
    }


    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(SHELF, new ShelfDto());
        return "shelf/add";
    }


    @PostMapping("/add")
    public String addPost(@ModelAttribute ShelfDto shelfDto,
                          RedirectAttributes redirectAttributes) {

        ShelfDto response = shelfService.save(shelfDto);

        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(
                    ERROR_MESSAGE,
                    response.getMessage()
            );
            return "redirect:/shelf/add";
        }

        redirectAttributes.addFlashAttribute(
                SUCCESS_MESSAGE,
                "Shelf added successfully"
        );

        return REDIRECT_LIST;
    }


    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        model.addAttribute(SHELF, shelfService.findByIdentifier(identifier));
        return "shelf/edit";
    }


    @PostMapping("/update")
    public String update(@ModelAttribute ShelfDto shelfDto,
                         RedirectAttributes redirectAttributes) {

        ShelfDto response = shelfService.update(shelfDto);

        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(
                    ERROR_MESSAGE,
                    response.getMessage()
            );
            return "/shelf/edit";
        }

        redirectAttributes.addFlashAttribute(
                SUCCESS_MESSAGE,
                "Shelf updated successfully"
        );

        return REDIRECT_LIST;
    }


    @PostMapping("/delete")
    public String delete(@RequestParam String identifier,
                         RedirectAttributes redirectAttributes) {

        shelfService.delete(identifier);

        redirectAttributes.addFlashAttribute(
                SUCCESS_MESSAGE,
                "Shelf deleted successfully"
        );

        return REDIRECT_LIST;
    }


    @PostMapping("/toggle")
    public String toggleShelf(@RequestParam String identifier,
                              RedirectAttributes redirectAttributes) {

        shelfService.toggleStatus(identifier);

        redirectAttributes.addFlashAttribute(
                SUCCESS_MESSAGE,
                "Shelf status updated successfully"
        );

        return REDIRECT_LIST;
    }
}