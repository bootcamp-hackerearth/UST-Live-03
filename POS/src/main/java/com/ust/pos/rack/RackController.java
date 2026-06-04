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

    public static final String REDIRECT_RACK_LIST = "redirect:/rack/list";
    public static final String SHELVES = "shelves";

    @Autowired
    private RackService rackService;

    @Autowired
    private ShelfService shelfService;

    @GetMapping("/list")
    public String home(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("racks", rackService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "rack/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("rackDto", new RackDto());
        model.addAttribute(SHELVES, shelfService.getActiveShelves());
        return "rack/add";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute RackDto rackDto, Model model) {

        RackDto response = rackService.createRack(rackDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("messageType", "error");
            model.addAttribute(SHELVES, shelfService.getActiveShelves());
            return "rack/add";
        }

        return REDIRECT_RACK_LIST;
    }

    @GetMapping("/get")
    public String update(@RequestParam Long id, Model model) {

        RackDto response = rackService.getRack(id);

        if (!response.isSuccess()) {
            return REDIRECT_RACK_LIST;
        }

        model.addAttribute("rack", response);
        model.addAttribute(SHELVES, shelfService.getActiveShelves());
        return "rack/rack";
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute RackDto rackDto) {
        rackService.updateRack(rackDto);
        return REDIRECT_RACK_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam Long id) {
        rackService.deleteRack(id);
        return REDIRECT_RACK_LIST;
    }
}