package com.ust.pos.api.rack;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.rack.service.RackService;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("rackApiController")
@RequestMapping("/api/racks")
public class RackController extends BaseController {

    @Autowired
    private RackService rackService;

    @Autowired
    private ShelfService shelfService;

    @PostMapping("/list")
    public ResponseEntity<List<RackDto>> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        List<RackDto> racks = rackService.findAll(pageable);
        return ResponseEntity.ok(racks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RackDto> getById(@PathVariable Long id) {
        RackDto response = rackService.getRack(id);
        if (response == null || !response.isSuccess()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<RackDto> save(@RequestBody RackDto rackDto) {
        RackDto response = rackService.createRack(rackDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<RackDto> update(@PathVariable Long id, @RequestBody RackDto rackDto) {
        rackDto.setId(id);
        RackDto response = rackService.updateRack(rackDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        try {
            rackService.deleteRack(id);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @GetMapping("/shelves")
    public ResponseEntity<List<ShelfDto>> getShelves() {
        List<ShelfDto> shelves = shelfService.getActiveShelves();
        return ResponseEntity.ok(shelves);
    }
}