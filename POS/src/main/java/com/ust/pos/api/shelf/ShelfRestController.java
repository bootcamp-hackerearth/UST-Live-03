package com.ust.pos.api.shelf;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/shelf")
public class ShelfRestController extends BaseController {

    private final ShelfService shelfService;

    public ShelfRestController(ShelfService shelfService) {
        this.shelfService = shelfService;
    }

    @PostMapping("/list")
    public WsDto<ShelfDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return shelfService.findAll(pageable);
    }

    @PostMapping("/add")
    public ShelfDto addPost(@RequestBody ShelfDto shelfDto) {
        return shelfService.save(shelfDto);
    }

    @PostMapping("/get")
    public ShelfDto update(@RequestBody String identifier) {
        return shelfService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ShelfDto updatePost(@RequestBody ShelfDto shelfDto) {
        return shelfService.update(shelfDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody String identifier) {
        try {
            shelfService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle")
    public String toggleStatus(@RequestBody String identifier) {
        shelfService.toggleStatus(identifier);
        return identifier;
    }
}
