package com.ust.pos.api.shelf;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelf")
public class ShelfControllerApi extends BaseController {

    @Autowired
    private ShelfService shelfService;

    @PostMapping("/list")
    public WsDto<ShelfDto> list(@RequestBody PaginationDto pagination) {
        Pageable pageable = getPageable(pagination.getPage(), pagination.getSizePerPage(),
                pagination.getSortDirection(), pagination.getSortfield());
        return shelfService.findAll(pageable);
    }

    @PostMapping("/add")
    public ShelfDto add(@RequestBody ShelfDto shelfDto) {
        return shelfService.save(shelfDto);
    }

    @GetMapping("/get")
    public ShelfDto get(@RequestParam String identifier) {
        return shelfService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ShelfDto update(@RequestBody ShelfDto shelfDto) {
        return shelfService.update(shelfDto);
    }

    @PostMapping("/delete")
    public ShelfDto delete(@RequestBody ShelfDto shelfDto) {
        ShelfDto response = new ShelfDto();
        try {
            shelfService.delete(shelfDto.getIdentifier());
            response.setSuccess(true);
            response.setMessage("Shelf deleted successfully");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Delete failed");
        }
        return response;
    }

    @GetMapping("/active")
    public List<ShelfDto> activeShelves() {
        return shelfService.getActiveShelves();
    }

    @GetMapping("/toggle")
    public ShelfDto toggle(@RequestParam String identifier) {
        return shelfService.toggleStatus(identifier);
    }
}