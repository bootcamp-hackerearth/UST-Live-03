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
    public String add(Model model, @ModelAttribute ShelfDto shelfDto, Pageable pageable) {
        model.addAttribute(SHELFS, shelfService.findAll(pageable));
        return "shelf/add";
    }

    @PostMapping("/add")
    public String doadd(Model model, @ModelAttribute ShelfDto shelfDto) {
        ShelfDto shelfDto1 = shelfService.save(shelfDto);
        if (!shelfDto1.isSuccess()) {
            model.addAttribute("message", shelfDto1.getMessage());
            model.addAttribute(SHELFS, shelfService.findAll(null));
            return "shelf/add";
        }
        return REDIRECT_SHELF_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ShelfDto shelfDto = shelfService.findByIdentifier(identifier);
        model.addAttribute(SHELFS, shelfDto);
        return "shelf/shelf";
    }

    @PostMapping("/update")
    public String doupdate(Model model, @ModelAttribute ShelfDto shelfDto, Pageable pageable) {
        ShelfDto shelfDto1 = shelfService.update(shelfDto);
        if (!shelfDto1.isSuccess()) {
            model.addAttribute("messsage", shelfDto1.getMessage());
            model.addAttribute(SHELFS, shelfService.findAll(pageable));
            return "shelf/shelf";
        }
        return REDIRECT_SHELF_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        shelfService.toggleStatus(identifier);
        return REDIRECT_SHELF_LIST;
    }
}