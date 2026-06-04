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

    public static final String MESSAGE = "message";
    public static final String SHELVES = "shelves";
    public static final String RACK_DTO = "rackDto";
    private static final String REDIRECT_LIST = "redirect:/rack/list";
    @Autowired
    private RackService rackService;

    @Autowired
    private ShelfService shelfService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("racks", rackService.findAll(pageable));
        return "rack/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(RACK_DTO, new RackDto());
        model.addAttribute(SHELVES, shelfService.getActiveShelves());
        return "rack/add";
    }

    @PostMapping("/add")
    public String save(@ModelAttribute RackDto rackDto, Model model) {
        RackDto response = rackService.save(rackDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute(RACK_DTO, rackDto);
            model.addAttribute(SHELVES, shelfService.getActiveShelves());
            return "rack/add";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/edit")
    public String edit(@RequestParam String identifier, Model model) {
        RackDto dto = rackService.findByIdentifier(identifier);
        model.addAttribute(RACK_DTO, dto);
        model.addAttribute(SHELVES, shelfService.getActiveShelves());
        return "rack/rack";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute RackDto rackDto, Model model) {
        RackDto response = rackService.update(rackDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute(RACK_DTO, rackDto);
            model.addAttribute(SHELVES, shelfService.getActiveShelves());
            return "rack/rack";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        rackService.delete(identifier);
        return REDIRECT_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        rackService.toggleStatus(identifier);
        return REDIRECT_LIST;
    }
}