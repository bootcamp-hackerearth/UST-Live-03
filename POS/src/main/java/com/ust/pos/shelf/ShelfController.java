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

    public static final String REDIRECT_ROLE_LIST = "redirect:/shelf/list";
    public static final String SHELVES = "shelves";

    @Autowired
    private ShelfService shelfService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {

        model.addAttribute(SHELVES, shelfService.findAll(pageable));
        return "shelf/list";
    }

    @GetMapping("/add")
    public String add(Model model) {

        model.addAttribute("shelfDto", new ShelfDto());
        return "shelf/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ShelfDto shelfDto) {

        ShelfDto response = shelfService.save(shelfDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "shelf/add";
        }

        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {

        ShelfDto response = shelfService.findByIdentifier(identifier);
        model.addAttribute("shelfDto", response);

        return "shelf/shelf";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ShelfDto shelfDto) {

        ShelfDto response = shelfService.update(shelfDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "shelf/shelf";
        }

        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {

        shelfService.delete(identifier);
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/toggle")
    @ResponseBody
    public String toggle(@RequestParam String identifier) {

        shelfService.toggleStatus(identifier);
        return REDIRECT_ROLE_LIST;
    }
}


