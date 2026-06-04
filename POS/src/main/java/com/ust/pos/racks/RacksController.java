package com.ust.pos.racks;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.racks.service.RacksService;
import com.ust.pos.shelves.service.ShelvesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/racks")
public class RacksController {

    public static final String RACKS_ADD = "racks/add";
    public static final String RACKS = "racks";
    public static final String SHELVES = "shelves";
    public static final String REDIRECT_RACKS_LIST = "redirect:/racks/list";
    @Autowired
    private RacksService racksService;

    @Autowired
    private ShelvesService shelvesService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(RACKS, racksService.findAll(pageable));
        return "racks/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(RACKS, new RacksDto());
        model.addAttribute(SHELVES, shelvesService.findActiveShelves());
        return RACKS_ADD;
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute RacksDto racksDto) {
        RacksDto response = racksService.save(racksDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(SHELVES, shelvesService.findActiveShelves());
            return RACKS_ADD;
        }

        return REDIRECT_RACKS_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        model.addAttribute(RACKS, racksService.findByIdentifier(identifier));
        model.addAttribute(SHELVES, shelvesService.findActiveShelves());
        return RACKS_ADD;
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute RacksDto racksDto) {
        RacksDto response = racksService.update(racksDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(SHELVES, shelvesService.findActiveShelves());
            return RACKS_ADD;
        }

        return REDIRECT_RACKS_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        racksService.delete(identifier);
        return REDIRECT_RACKS_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        racksService.toggleStatus(identifier);
        return REDIRECT_RACKS_LIST;
    }
}