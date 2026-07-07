package com.ust.pos.api.shelf;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.shelf.service.ShelfService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController("shelfApiController")
@RequestMapping("/api/shelves")
public class ShelfController extends BaseController {
    private final ShelfService shelfService;

    public ShelfController(ShelfService shelfService) {
        this.shelfService = shelfService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin')")
    public WsDto<ShelfDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Shelf> spec = buildGlobalSearchSpec(Shelf.class, paginationDto.getKeyword());
            return shelfService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return shelfService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ShelfDto> getById(@PathVariable Long id) {
        ShelfDto response = shelfService.getShelf(id);
        if (response == null || !response.isSuccess()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ShelfDto> save(@RequestBody ShelfDto shelfDto) {
        ShelfDto response = shelfService.createShelf(shelfDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ShelfDto> update(@PathVariable Long id, @RequestBody ShelfDto shelfDto) {
        shelfDto.setId(id);
        ShelfDto response = shelfService.updateShelf(shelfDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        try {
            shelfService.deleteShelf(id);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @PostMapping("/toggle/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ShelfDto> toggleStatus(@PathVariable Long id) {
        ShelfDto response = shelfService.toggleStatus(id);
        return ResponseEntity.ok(response);
    }
}