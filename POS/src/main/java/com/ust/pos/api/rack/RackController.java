package com.ust.pos.api.rack;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Rack;
import com.ust.pos.rack.service.RackService;
import com.ust.pos.shelf.service.ShelfService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("rackApiController")
@RequestMapping("/api/racks")
public class RackController extends BaseController {
    private final RackService rackService;
    private final ShelfService shelfService;

    public RackController(RackService rackService, ShelfService shelfService) {
        this.rackService = rackService;
        this.shelfService = shelfService;
    }

    @PostMapping("/list")
    public WsDto<RackDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Rack> spec = buildGlobalSearchSpec(Rack.class, paginationDto.getKeyword());
            return rackService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return rackService.findAll(pageable);
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

    @PutMapping("/update/{id}")
    public ResponseEntity<RackDto> update(@PathVariable Long id, @RequestBody RackDto rackDto) {
        rackDto.setId(id);
        RackDto response = rackService.updateRack(rackDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
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