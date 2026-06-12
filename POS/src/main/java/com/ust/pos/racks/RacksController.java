package com.ust.pos.racks;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.racks.service.RacksService;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rack")
public class RacksController {

    public static final String REDIRECT_LIST = "redirect:/rack/list";
    public static final String SUCCESS_MESSAGE = "successMessage";

    @Autowired
    private RacksService racksService;

    @Autowired
    private ShelfService shelfService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("racks", racksService.findAll(pageable));
        return "racks/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("rack", new RacksDto());
        model.addAttribute("shelves", shelfService.findAllActive());
        return "racks/add";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute RacksDto racksDto, RedirectAttributes redirectAttributes) {
        RacksDto response = racksService.save(racksDto);

        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    response.getMessage()
            );
            return "redirect:/rack/add";
        }

        redirectAttributes.addFlashAttribute(
                SUCCESS_MESSAGE,
                "Rack added successfully"
        );
        return REDIRECT_LIST;
    }

    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        model.addAttribute("rack", racksService.findByIdentifier(identifier));
        model.addAttribute("shelves", shelfService.findAllActive());

        return "rack/rack";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute RacksDto racksDto, RedirectAttributes redirectAttributes) {
        RacksDto response = racksService.update(racksDto);

        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute("errorMessage", response.getMessage());
            return redirectToEdit(racksDto);
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, getStatusMessage(racksDto));
        return REDIRECT_LIST;
    }

    private String redirectToEdit(RacksDto rackDto) {
        return "redirect:/rack/edit?id=" + rackDto.getId();
    }

    private String getStatusMessage(RacksDto rackDto) {
        return Boolean.TRUE.equals(rackDto.getStatus())
                ? "Rack activated successfully"
                : "Rack deactivated successfully";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier, RedirectAttributes redirectAttributes) {
        racksService.delete(identifier);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Rack deleted successfully"
        );
        return REDIRECT_LIST;
    }
}