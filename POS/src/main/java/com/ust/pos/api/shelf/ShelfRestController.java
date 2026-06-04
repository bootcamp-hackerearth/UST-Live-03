package com.ust.pos.api.shelf;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/shelf")
public class ShelfRestController extends BaseController {

    @Autowired
    ShelfService shelfService;

    @PostMapping("/list")
    public List<ShelfDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return shelfService.findAll(pageable);
    }

    @GetMapping("/add")
    public ShelfDto add(@RequestParam String identifier) {
        return shelfService.findByIdentifier(identifier);
    }

    @PostMapping("/add")
    public ShelfDto addPost(@RequestBody ShelfDto shelfDto) {
        return shelfService.save(shelfDto);
    }

    @GetMapping("/get")
    public ShelfDto update(Model model, @RequestParam String identifier) {
        return shelfService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ShelfDto updatePost(@RequestBody ShelfDto shelfDto) {
        return shelfService.update(shelfDto);
    }

    @GetMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            shelfService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}