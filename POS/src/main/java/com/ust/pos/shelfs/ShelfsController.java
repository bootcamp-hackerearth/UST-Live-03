package com.ust.pos.shelfs;

import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.shelfs.sevice.ShelfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/shelfs")
public class ShelfsController {
    public static final String SHELVES = "shelves";
    public static final String REDIRECT_SHELFS_LIST = "redirect:/shelfs/list";
    @Autowired
    ShelfsService shelfsService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(SHELVES, shelfsService.findAll(pageable));
        return "shelfs/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ShelfsDto shelfsDto) {
        model.addAttribute(SHELVES, shelfsDto);
        return "shelfs/add";
    }

    @PostMapping("/add")
    public String addshelfs(Model model, @ModelAttribute ShelfsDto shelfsDto) {
        shelfsService.save(shelfsDto);
        model.addAttribute(SHELVES, shelfsDto);
        return REDIRECT_SHELFS_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        shelfsService.delete(identifier);
        return REDIRECT_SHELFS_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ShelfsDto response = shelfsService.findByIdentifier(identifier);
        model.addAttribute(SHELVES, response);
        return "shelfs/shelfs";
    }

    @PostMapping("/update")
    public String updatePrice(Model model, @ModelAttribute ShelfsDto shelfsDto) {
        ShelfsDto response = shelfsService.update(shelfsDto);
        model.addAttribute(SHELVES, response);
        if (!response.isSuccess()) {
            model.addAttribute(SHELVES, response);
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_SHELFS_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status) {
        shelfsService.changeToggleStatus(identifier, status);
        return REDIRECT_SHELFS_LIST;
    }
}
