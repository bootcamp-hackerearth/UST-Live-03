package com.ust.pos.shelf;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/shelf")
public class ShelfController extends BaseController {
    public static final String REDIRECT_SHELF_LIST = "redirect:/shelf/list";
    private static final String MESSAGE = "message";

    @Autowired
    private ShelfService shelfService;

    @GetMapping("/add")
    public String add() {
        return "shelf/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ShelfDto shelfDto) {
        ShelfDto response = shelfService.save(shelfDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            return "redirect:/shelf/add";
        }
        return REDIRECT_SHELF_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("shelves", shelfService.findAll(pageable));
        return "shelf/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ShelfDto response = shelfService.findByIdentifier(identifier);
        model.addAttribute("shelf", response);
        return "shelf/shelf";
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute ShelfDto shelfDto) {
        ShelfDto response = shelfService.update(shelfDto);
        if (!response.isSuccess()) {
            return REDIRECT_SHELF_LIST;
        }
        return "redirect:/shelf/get?identifier=" + shelfDto.getIdentifier();
    }

    @PostMapping("/toggle")
    @ResponseBody
    public void toggleStatus(@RequestParam String identifier) {
        shelfService.toggleStatus(identifier);
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        shelfService.delete(identifier);
        return REDIRECT_SHELF_LIST;
    }
}

