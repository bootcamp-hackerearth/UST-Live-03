package com.ust.pos.racks;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.racks.service.RacksService;
import com.ust.pos.shelfs.service.ShelfsService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/racks")
public class RacksController {

    public static final String REDIRECT_RACKS_LIST = "redirect:/racks/list";
    public static final String NODES = "nodes";
    public static final String RACKS_ADD = "racks/add";
    public static final String SHELFS = "shelfs";

    private final RacksService racksService;
    private final NodeService nodeService;
    private final ShelfsService shelfsService;

    public RacksController(RacksService racksService, NodeService nodeService, ShelfsService shelfsService) {
        this.racksService = racksService;
        this.nodeService = nodeService;
        this.shelfsService = shelfsService;
    }

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("rackss", racksService.findAll(pageable));
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        return "racks/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute RacksDto racksDto) {
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        model.addAttribute(SHELFS, shelfsService.findAllActive());
        return RACKS_ADD;
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute RacksDto racksDto) {
        RacksDto response = racksService.save(racksDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(NODES, nodeService.getNodesForRoles());
            model.addAttribute(SHELFS, shelfsService.findAllActive());
            return RACKS_ADD;
        }
        return REDIRECT_RACKS_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        RacksDto response = racksService.findByIdentifier(identifier);
        model.addAttribute("racks", response);
        model.addAttribute(NODES, nodeService.getNodesForRoles());
        model.addAttribute(SHELFS, shelfsService.findAllActive());
        return "racks/racks";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute RacksDto racksDto) {
        RacksDto response = racksService.update(racksDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(NODES, nodeService.getNodesForRoles());
            model.addAttribute(SHELFS, shelfsService.findAllActive());
            return "racks/racks";
        }
        return REDIRECT_RACKS_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        racksService.delete(identifier);
        return REDIRECT_RACKS_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        racksService.toggleStatus(identifier);
        return REDIRECT_RACKS_LIST;
    }
}
