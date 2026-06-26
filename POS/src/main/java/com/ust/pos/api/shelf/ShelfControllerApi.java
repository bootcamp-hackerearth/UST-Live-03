package com.ust.pos.api.shelf;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shelf")
public class ShelfControllerApi extends BaseController {

    private final ShelfService shelfService;

    public ShelfControllerApi(ShelfService shelfService) {
        this.shelfService = shelfService;
    }

    @GetMapping("/all")
    public List<ShelfDto> all() {
        return shelfService.findAll();
    }

    @GetMapping("/active")
    public List<ShelfDto> active() {
        return shelfService.findActive();
    }

    @PostMapping("/list")
    public WsDto<ShelfDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(), paginationDto.getSortField());
        Page<ShelfDto> pageResult = shelfService.findAll(pageable, paginationDto.getSearch());
        WsDto<ShelfDto> response = new WsDto<>();
        response.setContent(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPages(pageResult.getTotalPages());
        return response;
    }

    @PostMapping("/add")
    public ShelfDto addPost(@RequestBody ShelfDto shelfDto) {
        return shelfService.save(shelfDto);
    }

    @GetMapping("/get")
    public ShelfDto update(@RequestParam String identifier) {
        return shelfService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ShelfDto updatePost(@RequestBody ShelfDto shelfDto) {
        return shelfService.update(shelfDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            shelfService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    public boolean toggleStatus(@RequestParam String identifier) {
        try {
            shelfService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}