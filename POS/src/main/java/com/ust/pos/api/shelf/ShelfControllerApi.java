package com.ust.pos.api.shelf;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelf")
public class ShelfControllerApi extends BaseController {
    private final ShelfService shelfService;

    public ShelfControllerApi(ShelfService shelfService) {
        this.shelfService = shelfService;
    }

    @PostMapping("/list")
    public WsDto<ShelfDto> listCategories(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return shelfService.findAll(pageable);
    }

    @PostMapping("/add")
    public ShelfDto saveShelf(@RequestBody ShelfDto shelfDto) {

        return shelfService.save(shelfDto);

    }


    @GetMapping("/update")
    public ShelfDto showEditPage(@RequestParam String identifier) {

        return shelfService.findByIdentifier(identifier);

    }

    @PutMapping("/update")
    public ShelfDto saveEditedShelf(@RequestBody ShelfDto shelfDto) {

        return shelfService.update(shelfDto);
    }


    @DeleteMapping("/delete")
    public boolean deleteShelf(@RequestParam String identifier) {
        try {
            shelfService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    @GetMapping("/toggle")
    public boolean toggle(@RequestParam String identifier, boolean status) {
        try {
            shelfService.changeShelfStatus(identifier, status);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/findAllActive")
    public List<ShelfDto> allactive() {
        return shelfService.findActiveShelf();
    }

    @PostMapping("/changeStatus")
    public ShelfDto changestatus(@RequestBody ShelfDto shelfDto) {
        return shelfService.changeShelfStatus(shelfDto.getIdentifier(), shelfDto.isStatus());
    }
}