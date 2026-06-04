package com.ust.pos.shelf;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.rack.service.RackService;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/shelf")
public class ShelfController extends BaseController {

    private static final String SHELF_LIST = "shelf/list";
    private static final String SHELF_ADD = "shelf/add";
    private static final String SHELF_VIEW = "shelf/shelf";
    private static final String REDIRECT_SHELF_LIST = "redirect:/shelf/list";
    private static final String SHELF = "shelfs";
    private static final String ACTIVE_RACKS = "racks";

    @Autowired
    private ShelfService shelfService;

    @Autowired
    private RackService rackService;

    @GetMapping("/list")
    public String list(Model model, @ModelAttribute PaginationDto paginationDto) {
        model.addAttribute(SHELF, shelfService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return SHELF_LIST;
    }

    @GetMapping("/listactiveshelfs")
    public String listActiveShelfs(Model model) {
        model.addAttribute(SHELF, shelfService.findActiveShelfs());
        return SHELF_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ShelfDto shelfDto) {
        model.addAttribute(ACTIVE_RACKS, rackService.findActiveRacks());
        model.addAttribute(SHELF, shelfService.findAll(null));
        return SHELF_ADD;
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute ShelfDto shelfDto) {
        shelfService.save(shelfDto);
        return REDIRECT_SHELF_LIST;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public ShelfDto toggleStatus(@RequestBody ShelfDto shelfDto) {
        return shelfService.updateStatus(shelfDto.getIdentifier(), shelfDto.isStatus());
    }

    @GetMapping("/get")
    public String update(@RequestParam String identifier, Model model) {
        model.addAttribute(ACTIVE_RACKS, rackService.findActiveRacks());
        model.addAttribute("shelfDto", shelfService.findByIdentifier(identifier));
        return SHELF_VIEW;
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute ShelfDto shelfDto) {
        shelfService.update(shelfDto);
        return REDIRECT_SHELF_LIST;
    }


    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        shelfService.delete(identifier);
        return REDIRECT_SHELF_LIST;
    }
}
