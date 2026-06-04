package com.ust.pos.rack;

import com.ust.pos.dto.RackDto;
import com.ust.pos.rack.service.RackService;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/rack")
@Controller
public class RackController {

    public static final String REDIRECT_RACK_LIST = "redirect:/rack/list";
    public static final String SHELF = "shelf";
    public static final String RACKS = "racks";

    @Autowired
    private RackService rackService;
    @Autowired
    private ShelfService shelfService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(RACKS, rackService.findAll(pageable));
        model.addAttribute(SHELF, shelfService.findActiveShelves());
        return "rack/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(RACKS, new RackDto());
        model.addAttribute(SHELF, shelfService.findActiveShelves());
        return "rack/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute RackDto rackDto) {
        RackDto response = rackService.save(rackDto);
        if (!response.isSuccess()) {
            model.addAttribute(RACKS, rackDto);
            model.addAttribute("message", response.getMessage());
            model.addAttribute(SHELF, shelfService.findActiveShelves());
            return "rack/add";
        }
        return REDIRECT_RACK_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        RackDto response = rackService.findByIdentifier(identifier);
        model.addAttribute(SHELF, shelfService.findActiveShelves());
        model.addAttribute(RACKS, response);
        return "rack/rack";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute RackDto rackDto) {
        RackDto response = rackService.update(rackDto);
        if (!response.isSuccess()) {
            model.addAttribute(RACKS, rackDto);
            model.addAttribute("message", response.getMessage());
            model.addAttribute(SHELF, shelfService.findActiveShelves());
            return "rack/rack";
        }
        return REDIRECT_RACK_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        rackService.delete(identifier);
        return REDIRECT_RACK_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        rackService.toggleStatus(identifier);
        return REDIRECT_RACK_LIST;
    }

}
