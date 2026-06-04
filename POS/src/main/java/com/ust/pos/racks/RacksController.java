package com.ust.pos.racks;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.racks.service.RacksService;
import com.ust.pos.shelfs.service.ShelfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/racks")
public class RacksController {

    public static final String REDIRECT_RACKS_LIST = "redirect:/racks/list";

    private static final String SHELVES = "shelves";

    @Autowired
    private RacksService racksService;

    @Autowired
    private ShelfsService shelfsService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("rackss", racksService.findAll(pageable));
        return "racks/list";
    }

    @GetMapping("/add")
    public String add(Model model,@ModelAttribute RacksDto racksDto) {
        model.addAttribute(SHELVES, shelfsService.findIfTrue());
        return "racks/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute RacksDto racksDto) {
        RacksDto response = racksService.save(racksDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(SHELVES, shelfsService.findIfTrue());
            return "racks/add";
        }
        return REDIRECT_RACKS_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        RacksDto response = racksService.findByIdentifier(identifier);
        model.addAttribute("racks", response);
        model.addAttribute(SHELVES, shelfsService.findIfTrue());
        return "racks/racks";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute RacksDto racksDto) {
        RacksDto response = racksService.update(racksDto);
        if (!response.isSuccess()) {
            model.addAttribute("racks", response);
            model.addAttribute("message", response.getMessage());
            model.addAttribute(SHELVES, shelfsService.findIfTrue());
            return "racks/racks";
        }
        return REDIRECT_RACKS_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        racksService.delete(identifier);
        return REDIRECT_RACKS_LIST;
    }

    @PostMapping("/toggle-status")
    @ResponseBody
    public void toggle(@RequestParam String identifier) {
        racksService.toggleStatus(identifier);
    }
}