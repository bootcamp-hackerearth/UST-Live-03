package com.ust.pos.racks;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.racks.service.RacksService;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/racks")
public class RacksController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/racks/list";
    public static final String RACKS = "racks";
    public static final String SHELVES = "shelves";

    @Autowired
    private RacksService racksService;

    @Autowired
    private ShelfService shelfService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {

        model.addAttribute(RACKS, racksService.findAll(pageable));
        return "racks/list";
    }

    @GetMapping("/add")
    public String add(Model model) {

        model.addAttribute(SHELVES, shelfService.findActiveShelf());
        model.addAttribute("racksDto", new RacksDto());

        return "racks/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute RacksDto racksDto) {

        RacksDto response = racksService.save(racksDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(SHELVES, shelfService.findActiveShelf());
            return "racks/add";
        }

        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {

        RacksDto response = racksService.findByIdentifier(identifier);
        model.addAttribute(SHELVES, shelfService.findActiveShelf());
        model.addAttribute("racksDto", response);

        return "racks/racks";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute RacksDto racksDto) {

        RacksDto response = racksService.update(racksDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(SHELVES, shelfService.findActiveShelf());
            return "racks/racks";
        }

        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {

        racksService.delete(identifier);
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/toggle")
    @ResponseBody
    public String toggle(@RequestParam String identifier) {

        racksService.toggleStatus(identifier);
        return REDIRECT_ROLE_LIST;
    }
}


