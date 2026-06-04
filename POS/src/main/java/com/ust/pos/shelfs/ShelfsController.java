package com.ust.pos.shelfs;

import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.shelfs.service.ShelfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/shelfs")
public class ShelfsController {

    public static final String REDIRECT_SHELFS_LIST = "redirect:/shelfs/list";

    @Autowired
    private ShelfsService shelfsService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("shelfss", shelfsService.findAll(pageable));
        return "shelfs/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute ShelfsDto shelfsDto) {
        return "shelfs/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ShelfsDto shelfsDto) {
        ShelfsDto response = shelfsService.save(shelfsDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "shelfs/add";
        }
        return REDIRECT_SHELFS_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ShelfsDto response = shelfsService.findByIdentifier(identifier);
        model.addAttribute("shelfs", response);
        return "shelfs/shelfs";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ShelfsDto shelfsDto) {
        ShelfsDto response = shelfsService.update(shelfsDto);
        if (!response.isSuccess()) {
            model.addAttribute("shelfs", response);
            model.addAttribute("message", response.getMessage());
            return "shelfs/shelfs";
        }
        return REDIRECT_SHELFS_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        shelfsService.delete(identifier);
        return REDIRECT_SHELFS_LIST;
    }

    @PostMapping("toggle-status")
    @ResponseBody
    public void toggle(@RequestParam String identifier) {
        shelfsService.toggleStatus(identifier);
    }
}