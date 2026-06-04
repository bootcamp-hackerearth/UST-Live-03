package com.ust.pos.rack;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RackDto;
import com.ust.pos.rack.service.RackService;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/rack")
public class RackController extends BaseController {

    private static final String RACK_LIST = "rack/list";
    private static final String RACK_ADD = "rack/add";
    private static final String RACK_VIEW = "rack/rack";
    private static final String REDIRECT_RACK_LIST = "redirect:/rack/list";

    @Autowired
    private RackService rackService;

    @Autowired
    private ShelfService shelfService;

    @GetMapping("/list")
    public String list(Model model, @ModelAttribute PaginationDto paginationDto) {
        model.addAttribute("racks", rackService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return RACK_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute RackDto rackDto) {
        model.addAttribute("shelves", shelfService.findActiveShelfs());
        return RACK_ADD;
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute RackDto rackDto) {
        rackService.save(rackDto);
        return REDIRECT_RACK_LIST;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public RackDto toggleStatus(@RequestBody RackDto rackDto) {
        return rackService.updateStatus(rackDto.getIdentifier(), rackDto.isStatus());
    }

    @GetMapping("/get")
    public String update(@RequestParam String identifier, Model model) {
        model.addAttribute("rackDto", rackService.findByIdentifier(identifier));
        model.addAttribute("shelves", shelfService.findActiveShelfs());
        return RACK_VIEW;
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute RackDto rackDto) {
        rackService.update(rackDto);
        return REDIRECT_RACK_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        rackService.delete(identifier);
        return REDIRECT_RACK_LIST;
    }
}
