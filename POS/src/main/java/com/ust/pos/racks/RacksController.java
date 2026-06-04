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

    public static final String SHELFS = "shelfs";
    public static final String REDIRECT_RACKS_LIST = "redirect:/racks/list";

    @Autowired
    private RacksService racksService;

    @Autowired
    private ShelfService shelfService;

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("rack", new RacksDto());
        model.addAttribute(SHELFS, shelfService.findAllActive());
        return "racks/add";
    }

    @PostMapping("/add")
    public String addPost(Model model,
                          @ModelAttribute("rack") RacksDto racksDto) {
        RacksDto response = racksService.save(racksDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("racks", racksDto);
            model.addAttribute(SHELFS, shelfService.findAllActive());
            return "racks/add";
        }
        return REDIRECT_RACKS_LIST;
    }

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("racks", racksService.findAll(pageable));
        model.addAttribute(SHELFS, shelfService.findAllActive());
        return "racks/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        RacksDto response = racksService.findByIdentifier(identifier);
        model.addAttribute("rack", response);
        model.addAttribute(SHELFS, shelfService.findAllActive());
        return "racks/racks";
    }

    @PostMapping("/update")
    public String updatePost(Model model,
                             @ModelAttribute("rack") RacksDto racksDto) {
        RacksDto response = racksService.update(racksDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(SHELFS, shelfService.findAllActive());
            return "racks/racks";
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
