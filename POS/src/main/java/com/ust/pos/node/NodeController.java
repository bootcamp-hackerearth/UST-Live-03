package com.ust.pos.node;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.node.service.NodeService;
import com.ust.pos.role.service.RoleService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/node")
public class NodeController extends BaseController {

    public static final String REDIRECT_NODE_LIST = "redirect:/node/list";

    @Autowired
    private RoleService roleService;

    @Autowired
    private NodeService nodeService;

    @GetMapping("/list")
    public String home(Model model,@ModelAttribute PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),paginationDto.getSortField());
        model.addAttribute("nodes", nodeService.findAll(pageable));
        return "node/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute NodeDto nodeDto) {
        model.addAttribute("roles", roleService.findAll(null));
        return "node/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute NodeDto nodeDto) {
        NodeDto response = nodeService.save(nodeDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier, @ModelAttribute PaginationDto paginationDto) {
        NodeDto response = nodeService.findByIdentifier(identifier);
        Pageable pageable = getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),paginationDto.getSortField());
        model.addAttribute("node", response);
        model.addAttribute("roles", roleService.findAll(pageable));
        return "node/node";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute NodeDto nodeDto) {
        nodeService.update(nodeDto);
        return REDIRECT_NODE_LIST;
    }

    @GetMapping("/delete")
    @Transactional
    public String delete(Model model, @RequestParam String identifier) {
        nodeService.delete(identifier);
        return REDIRECT_NODE_LIST;
    }
}