package com.ust.pos.rack;

import com.ust.pos.dto.RackDto;
import com.ust.pos.rack.service.RackService;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/rack")
public class RackController {
    public static final String RACKS = "racks";
    public static final String RACK_LIST = "rack/list";
    public static final String MESSAGE = "message";

    @Autowired
    RackService rackService;

    @Autowired
    ShelfService shelfService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(RACKS, rackService.findAll(pageable));
        return RACK_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute RackDto rackDto) {
        model.addAttribute("shelves", shelfService.findActiveStatus());
        return "rack/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute RackDto rackDto, Pageable pageable) {
        RackDto response = rackService.save(rackDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Rack Added Successfully");
        }
        model.addAttribute(RACKS, rackService.findAll(pageable));
        return RACK_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        RackDto response = rackService.findByIdentifier(identifier);
        model.addAttribute("shelves", shelfService.findActiveStatus());
        model.addAttribute("rack", response);
        return "rack/rack";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute RackDto rackDto, Pageable pageable) {
        RackDto response = rackService.update(rackDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Updated Successfully");
        }
        model.addAttribute(RACKS, rackService.findAll(pageable));
        return RACK_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier, Pageable pageable) {
        rackService.delete(identifier);
        model.addAttribute(RACKS, rackService.findAll(pageable));
        model.addAttribute(MESSAGE, "Rack deleted successfully");
        return RACK_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        rackService.toggleStatus(identifier);
        return "redirect:/rack/list";
    }
}
