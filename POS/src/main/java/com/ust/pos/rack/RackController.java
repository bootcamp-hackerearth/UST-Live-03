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
    public static final String SUCCESS_MESSAGE = "successMessage";

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
        model.addAttribute("shelves", shelfService.findAllActive());
        return "rack/add";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute RackDto rackDto, RedirectAttributes redirectAttributes) {
        RackDto response = rackService.save(rackDto);

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
        model.addAttribute("rack", rackService.findByIdentifier(identifier));
        model.addAttribute("shelves", shelfService.findAllActive());
        return "rack/rack";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute RackDto rackDto, RedirectAttributes redirectAttributes) {
        RackDto response = rackService.update(rackDto);

        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute("errorMessage", response.getMessage());
            return redirectToEdit(rackDto);
        }
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, getStatusMessage(rackDto));
        return REDIRECT_LIST;
    }

    private String redirectToEdit(RackDto rackDto) {
        return "redirect:/rack/edit?id=" + rackDto.getId();
    }

    private String getStatusMessage(RackDto rackDto) {
        return Boolean.TRUE.equals(rackDto.getStatus())
                ? "Rack activated successfully"
                : "Rack deactivated successfully";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier, RedirectAttributes redirectAttributes) {
        rackService.delete(identifier);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Rack deleted successfully"
        );
        return REDIRECT_LIST;
    }
}