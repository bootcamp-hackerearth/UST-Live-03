package com.ust.pos.shelfs;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.shelfs.service.ShelfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/shelf")
public class ShelfsController extends BaseController {

    public static final String REDIRECT_SHELF_LIST = "redirect:/shelf/list";
    public static final String SHELF = "shelf";

    @Autowired
    private ShelfsService shelfsService;

    @GetMapping("/list")
    public String home(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute(SHELF, shelfsService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "shelf/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(SHELF, new ShelfsDto());
        return "shelf/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ShelfsDto shelfDto) {
        ShelfsDto response = shelfsService.save(shelfDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(SHELF, shelfDto);
            return "shelf/add";
        }
        return REDIRECT_SHELF_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        PaginationDto paginationDto = new PaginationDto();
        ShelfsDto shelfDto = shelfsService.findByIdentifier(identifier);
        model.addAttribute(SHELF, shelfsService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        model.addAttribute("shelfDto", shelfDto);
        return "shelf/shelf";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ShelfsDto shelfsDto) {
        ShelfsDto response = shelfsService.update(shelfsDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute("shelfDto", shelfsDto);
            return "shelf/shelf";
        }
        return REDIRECT_SHELF_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        shelfsService.delete(identifier);
        return REDIRECT_SHELF_LIST;
    }

    @PostMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier) {
        shelfsService.toggleStatus(identifier);
        return REDIRECT_SHELF_LIST;
    }
}