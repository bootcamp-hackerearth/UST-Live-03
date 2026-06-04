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
    public static final String REDIRECT_RACK_LIST = "redirect:/rack/list";
    private static final String SHELF = "shelfs";
    @Autowired
    private RackService rackService;
    @Autowired
    private ShelfService shelfService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("racks", rackService.findAll(pageable));
        return "rack/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute RackDto rackDto) {
        model.addAttribute(SHELF, shelfService.findAllActive());
        return "rack/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute RackDto rackDto) {
        RackDto response = rackService.save(rackDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "rack/add";
        }
        return REDIRECT_RACK_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier, Pageable pageable) {
        RackDto response = rackService.findByIdentifier(identifier);
        model.addAttribute(SHELF, shelfService.findAll(pageable));
        model.addAttribute("rack", response);
        return "rack/rack";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute RackDto rackDto) {
        RackDto response = rackService.update(rackDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("rack", rackDto);
            return "rack/rack";
        }
        return REDIRECT_RACK_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        rackService.delete(identifier);
        return REDIRECT_RACK_LIST;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public String toggle(@RequestParam String identifier, boolean status) {
        rackService.updateStatus(identifier, status);
        return "success";
    }
}