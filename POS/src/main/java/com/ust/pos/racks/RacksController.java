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
    public static final String SHELF_LIST = "shelfList";
    public static final String REDIRECT_RACKS_LIST = "redirect:/racks/list";
    @Autowired
    RacksService racksService;
    @Autowired
    ShelfService shelfService;

    @GetMapping("/list")
    public String listCategories(Model model, Pageable pageable) {
        model.addAttribute("racks", racksService.findAll(pageable));
        model.addAttribute(SHELF_LIST, shelfService.findActiveShelf());
        return "racks/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("rackDto", new RacksDto());
        model.addAttribute(SHELF_LIST, shelfService.findActiveShelf());
        return "racks/add";
    }

    @PostMapping("/add")
    public String saveRacks(@ModelAttribute RacksDto racksDto) {
        racksService.save(racksDto);
        return REDIRECT_RACKS_LIST;
    }

    @GetMapping("/update")
    public String showEditPage(@RequestParam String identifier, Model model) {

        // racks being edited
        model.addAttribute("racksDto", racksService.findByIdentifier(identifier));
        model.addAttribute(SHELF_LIST, shelfService.findActiveShelf());

        // list used to populate Super Racks dropdown

        return "racks/racks";
    }

    @PostMapping("/update")
    public String saveEditedRacks(@ModelAttribute RacksDto racksDto) {

        racksService.update(racksDto);
        return REDIRECT_RACKS_LIST;
    }

    @GetMapping("/delete")
    public String deleteRacks(@RequestParam Long id) {
        racksService.deleteById(id);
        return REDIRECT_RACKS_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status) {
        racksService.changeRacksStatus(identifier, status);
        return REDIRECT_RACKS_LIST;
    }
}