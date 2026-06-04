package com.ust.pos.api.rack;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.rack.service.RackService;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/rack")
public class RackRestController extends BaseController {

    @Autowired
    RackService rackService;

    @Autowired
    ShelfService shelfService;

    @PostMapping("/list")
    public List<RackDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return rackService.findAll(pageable);
    }

    @GetMapping("/add")
    public List<ShelfDto> add(Model model, @ModelAttribute RackDto rackDto) {
        return shelfService.findActiveStatus();
    }

    @PostMapping("/add")
    public RackDto addPost(@RequestBody RackDto rackDto) {
        return rackService.save(rackDto);
    }

    @GetMapping("/get")
    public RackDto update(@RequestParam String identifier) {
        return rackService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public RackDto updatePost(Model model, @ModelAttribute RackDto rackDto) {
        return rackService.update(rackDto);
    }

    @GetMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            rackService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}

