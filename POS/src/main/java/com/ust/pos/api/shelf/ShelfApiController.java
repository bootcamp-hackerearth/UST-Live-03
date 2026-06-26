package com.ust.pos.api.shelf;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelf")
public class ShelfApiController extends BaseController {
    private final ShelfService shelfService;

    public ShelfApiController(ShelfService shelfService) {
        this.shelfService = shelfService;
    }

    @GetMapping("/list")
    public List<ShelfDto> list() {
        return shelfService.findAll();
    }

    @PostMapping("/list")
    public WsDto<ShelfDto> home(
            @RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortField());

        Page<ShelfDto> pageResult =
                shelfService.findAll(
                        paginationDto.getSearch(), pageable);

        WsDto<ShelfDto> response = new WsDto<>();

        response.setContent(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPages(pageResult.getTotalPages());

        return response;
    }

    @PostMapping("/add")
    public ShelfDto doadd(@RequestBody ShelfDto shelfDto) {
        return shelfService.save(shelfDto);
    }

    @GetMapping("/get")
    public ShelfDto update(@RequestParam String identifier) {
        return shelfService.findByIdentifier(identifier);

    }

    @PutMapping("/update")
    public ShelfDto doupdate(@RequestBody ShelfDto shelfDto) {
        return shelfService.update(shelfDto);
    }

    @PostMapping("status")
    public boolean updateStatus(@RequestParam String identifier, boolean status) {
        try {
            shelfService.updateStatusOnly(identifier, status);
        } catch (Exception e) {
            return false;
        }
        return true;
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
}
