package com.ust.pos.shelves;

import com.ust.pos.dto.ShelvesDto;
import com.ust.pos.shelves.service.ShelvesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/shelves")
public class ShelvesController {
    public static final String REDIRECT_SHELVES_LIST = "redirect:/shelves/list";
    @Autowired
    private ShelvesService shelvesService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("shelves", shelvesService.findAll(pageable));
        return "shelves/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ShelvesDto shelvesDto) {
        return "shelves/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ShelvesDto shelvesDto) {
        ShelvesDto response = shelvesService.save(shelvesDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "shelves/add";
        }
        return REDIRECT_SHELVES_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ShelvesDto response = shelvesService.findByIdentifier(identifier);
        model.addAttribute("shelves", response);
        return "shelves/shelves";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ShelvesDto shelvesDto) {
        ShelvesDto response = shelvesService.update(shelvesDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());

        }
        return REDIRECT_SHELVES_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        shelvesService.delete(identifier);
        return REDIRECT_SHELVES_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        shelvesService.toggleStatus(identifier);
        return REDIRECT_SHELVES_LIST;
    }
}