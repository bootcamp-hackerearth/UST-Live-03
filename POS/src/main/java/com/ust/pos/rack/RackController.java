package com.ust.pos.rack;

import com.ust.pos.dto.RackDto;
import com.ust.pos.rack.service.RackService;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rack")
public class RackController {

    public static final String REDIRECT_LIST = "redirect:/rack/list";
    public static final String MESSAGE = "successMessage";

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
        model.addAttribute("rack", new RackDto());
        model.addAttribute("shelves", shelfService.findIfTrue());
        return "rack/add";
    }


    @PostMapping("/add")
    public String addPost(@ModelAttribute RackDto rackDto,
                          RedirectAttributes redirectAttributes) {
        RackDto response = rackService.save(rackDto);
        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    response.getMessage()
            );
            return "redirect:/rack/add";
        }
        redirectAttributes.addFlashAttribute(
                MESSAGE,
                "Rack added successfully."
        );

        return REDIRECT_LIST;
    }


    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        model.addAttribute("rack", rackService.findByIdentifier(identifier));
        model.addAttribute("shelves", shelfService.findIfTrue());
        return "rack/edit";
    }


    @PostMapping("/update")
    public String update(@ModelAttribute RackDto rackDto,
                         RedirectAttributes redirectAttributes) {

        RackDto response = rackService.update(rackDto);

        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    response.getMessage()
            );
            return "redirect:/rack/edit";
        }
        redirectAttributes.addFlashAttribute(
                MESSAGE,
                Boolean.TRUE.equals(rackDto.isStatus())
                        ? "Rack activated successfully"
                        : "Rack deactivated successfully");
        return REDIRECT_LIST;
    }


    @GetMapping("/delete")
    public String delete(@RequestParam String identifier,
                         RedirectAttributes redirectAttributes) {

        rackService.delete(identifier);
        redirectAttributes.addFlashAttribute(
                MESSAGE,
                "Rack deleted successfully"
        );
        return REDIRECT_LIST;
    }

    @PostMapping("/toggle")
    public String togglePRack(@RequestParam String identifier) {
        rackService.toggleStatus(identifier);
        return REDIRECT_LIST;
    }
}
