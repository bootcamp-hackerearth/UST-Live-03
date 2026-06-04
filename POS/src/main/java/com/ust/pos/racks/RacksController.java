package com.ust.pos.racks;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.racks.service.RacksService;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/racks")
public class RacksController {

    public static final String REDIRECT_RACKS_LIST = "redirect:/racks/list";

    private static final String RACKS = "racks";

    @Autowired
    private RacksService racksService;

    @Autowired
    private ShelfService shelfService;

    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute(RACKS, racksService.findAll());
        return "racks/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute RacksDto racksDto) {
        model.addAttribute(RACKS, racksService.findAll());
        model.addAttribute("shelf", shelfService.findAll());
        return "racks/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute RacksDto racksDto) {
        RacksDto response = racksService.save(racksDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(RACKS, racksService.findAll());
            return "racks/add";
        }
        return REDIRECT_RACKS_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        RacksDto racksDto = racksService.findByIdentifier(identifier);
        model.addAttribute(RACKS, racksService.findAll());
        model.addAttribute("shelf", shelfService.findAll());
        model.addAttribute("racksDto", racksDto);
        return "racks/racks";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute RacksDto racksDto) {
        RacksDto response = racksService.update(racksDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("racksDto", racksDto);
            return "racks/racks";
        }
        return REDIRECT_RACKS_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        racksService.delete(identifier);
        return REDIRECT_RACKS_LIST;
    }

    @PostMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier) {
        racksService.toggleStatus(identifier);
        return REDIRECT_RACKS_LIST;
    }

}