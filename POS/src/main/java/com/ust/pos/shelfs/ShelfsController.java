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
@RequestMapping("/shelfs")
public class ShelfsController extends BaseController {
    public static final String REDIRECT_SHELFS_LIST= "redirect:/shelfs/list";
    public static final String SHELFS_DTO = "shelfsDto";
    @Autowired
    private ShelfsService shelfsService;

    @GetMapping("/list")
    public String home(Model model) {
        PaginationDto paginationDto = new PaginationDto();
        model.addAttribute("shelfs", shelfsService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField())));
        return "shelfs/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ShelfsDto shelfsDto) {
        model.addAttribute(SHELFS_DTO,  new ShelfsDto());
        return "shelfs/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute  ShelfsDto shelfsDto) {
        ShelfsDto response =shelfsService.save(shelfsDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(SHELFS_DTO, shelfsDto);
            return "shelfs/add";

        }
        return REDIRECT_SHELFS_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ShelfsDto response =shelfsService.findByIdentifier(identifier);
        model.addAttribute(SHELFS_DTO, response);
        return "shelfs/shelfs";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ShelfsDto shelfsDto) {
        ShelfsDto response =shelfsService.update(shelfsDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_SHELFS_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        shelfsService.delete(identifier);
        return REDIRECT_SHELFS_LIST;
    }
    @GetMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier) {
        shelfsService.toggleStatus(identifier);
        return REDIRECT_SHELFS_LIST;
    }
}
