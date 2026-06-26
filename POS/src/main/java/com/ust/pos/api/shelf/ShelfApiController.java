package com.ust.pos.api.shelf;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shelf")
public class ShelfApiController extends BaseController {

    private final ShelfService shelfService;

    public ShelfApiController(ShelfService shelfService) {
        this.shelfService = shelfService;
    }

    @PostMapping("/list")
    public WsDto<ShelfDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortField()
        );

        Page<ShelfDto> pageResult =
                shelfService.findAll(pageable, paginationDto.getSearch());

        WsDto<ShelfDto> output = new WsDto<>();
        output.setContent(pageResult.getContent());
        output.setPage(pageResult.getNumber());
        output.setSizePerPage(pageResult.getSize());
        output.setTotalPages(pageResult.getTotalPages());

        return output;
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