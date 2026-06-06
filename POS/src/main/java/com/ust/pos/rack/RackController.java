package com.ust.pos.rack;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RackDto;
import com.ust.pos.model.ShelfsRepository;
import com.ust.pos.rack.service.RackService;
import com.ust.pos.shelfs.service.ShelfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/rack")
public class RackController extends BaseController {

    private static final String REDIRECT_LIST = "redirect:/rack/list";

    @Autowired
    ShelfsRepository shelfsRepository;

    @Autowired
    private RackService rackService;

    @Autowired
    private ShelfsService shelfsService;

    @GetMapping("/list")
    public String list(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("racks", rackService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "rack/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("rackDto", new RackDto());
        model.addAttribute("shelfs", shelfsRepository.findByStatusTrue());
        return "rack/add";
    }

    @PostMapping("/add")
    public String save(Model model, @ModelAttribute RackDto rackDto) {
        RackDto response = rackService.save(rackDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "rack/add";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        RackDto dto = rackService.findByIdentifier(identifier);
        model.addAttribute("shelfs", shelfsRepository.findByStatusTrue());
        model.addAttribute("rackDto", dto);
        return "rack/rack";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute RackDto rackDto) {
        RackDto response = rackService.update(rackDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "rack/rack";
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        rackService.delete(identifier);
        return REDIRECT_LIST;
    }

    @PostMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier) {
        rackService.toggleStatus(identifier);
        return REDIRECT_LIST;
    }
}
