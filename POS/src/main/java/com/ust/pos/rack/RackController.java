package com.ust.pos.rack;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
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
    public static final String SHELFS = "shelfs";

    @Autowired
    RackService rackService;
    @Autowired
    ShelfService shelfService;

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute RackDto rackDto) {
        model.addAttribute(SHELFS, shelfService.findActiveShelves());
        return "rack/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute RackDto rackDto) {
        RackDto response = rackService.save(rackDto);
        if (!response.isSuccess()) {
            model.addAttribute("racks", rackService.findAll(null));
            model.addAttribute("message", response.getMessage());
            return "rack/add";
        }
        return REDIRECT_RACK_LIST;
    }

    @GetMapping("/list")
    public String home(Model model, @ModelAttribute PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        model.addAttribute("racks", rackService.findAll(pageable));
        model.addAttribute(SHELFS, shelfService.findActiveShelves());
        return "rack/list";
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        RackDto response = rackService.findByIdentifier(identifier);
        model.addAttribute("rack", response);
        model.addAttribute(SHELFS, shelfService.findActiveShelves());
        return "rack/rack";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute RackDto rackDto) {
        RackDto response = rackService.update(rackDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
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
    public RackDto toggleStatus(@RequestBody RackDto rackDto) {
        return rackService.toggleStatus(rackDto.getIdentifier(), rackDto.isStatus());
    }

}