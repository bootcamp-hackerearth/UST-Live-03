package com.ust.pos.racks;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.racks.service.RacksService;
import com.ust.pos.shelfs.sevice.ShelfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/rack")
public class RacksController {

    public static final String REDIRECT_RACK_LIST = "redirect:/rack/list";
    public static final String RACKS = "racks";

    @Autowired
    RacksService racksService;

    @Autowired
    ShelfsService shelfsService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(RACKS, racksService.findAll(pageable));
        return "rack/list";
    }

    @GetMapping("/add")
    public String add(org.springframework.ui.Model model, @ModelAttribute RacksDto racksDto) {
        model.addAttribute(RACKS, racksDto);
        model.addAttribute("shelves", shelfsService.findActiveStatus());
        return "rack/add";
    }

    @PostMapping("/add")
    public String addracks(Model model, @ModelAttribute RacksDto racksDto) {

        RacksDto response = racksService.save(racksDto);

        if (!response.isSuccess()) {
            model.addAttribute("racksDto", response);
            model.addAttribute("message", response.getMessage());
            return "rack/add";
        }

        return REDIRECT_RACK_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        racksService.delete(identifier);
        return REDIRECT_RACK_LIST;
    }

    @GetMapping("/get")
    public String update(org.springframework.ui.Model model, @RequestParam String identifier) {
        RacksDto response = racksService.findByIdentifier(identifier);
        model.addAttribute(RACKS, response);
        model.addAttribute("shelves", shelfsService.findActiveStatus());
        return "rack/rack";
    }

    @PostMapping("/update")
    public String updatePrice(Model model, @ModelAttribute RacksDto racksDto) {
        RacksDto response = racksService.update(racksDto);
        model.addAttribute(RACKS, response);
        if (!response.isSuccess()) {
            model.addAttribute("rack", response);
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_RACK_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status) {
        racksService.changeToggleStatus(identifier, status);
        return REDIRECT_RACK_LIST;
    }
}
