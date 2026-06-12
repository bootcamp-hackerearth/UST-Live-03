package com.ust.pos.rack;

import com.ust.pos.api.BaseController;
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
public class RackController extends BaseController {
    public static final String REDIRECT_RACK_LIST = "redirect:/rack/list";
    private static final String MESSAGE = "message";

    @Autowired
    private RackService rackService;

    @Autowired
    private ShelfService shelfService;

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("shelves", shelfService.findAllActive());
        return "rack/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute RackDto rackDto) {
        RackDto response = rackService.save(rackDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            return "redirect:/rack/add";
        }
        return REDIRECT_RACK_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("racks", rackService.findAll(pageable));
        return "rack/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        RackDto response = rackService.findByIdentifier(identifier);
        model.addAttribute("rack", response);
        model.addAttribute("shelves", shelfService.findAllActive());
        return "rack/rack";
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute RackDto rackDto) {
        RackDto response = rackService.update(rackDto);
        if (!response.isSuccess()) {
            return REDIRECT_RACK_LIST;
        }
        return "redirect:/rack/get?identifier=" + rackDto.getIdentifier();
    }

    @PostMapping("/toggle")
    @ResponseBody
    public void toggleStatus(@RequestParam String identifier) {
        rackService.toggleStatus(identifier);
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        rackService.delete(identifier);
        return REDIRECT_RACK_LIST;
    }
}
