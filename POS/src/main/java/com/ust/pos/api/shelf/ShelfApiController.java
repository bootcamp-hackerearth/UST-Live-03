package com.ust.pos.api.shelf;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.shelf.service.ShelfService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelf")
public class ShelfApiController extends BaseController {
    private final ShelfService shelfService;

    public ShelfApiController(ShelfService shelfService) {
        this.shelfService = shelfService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public WsDto<ShelfDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Shelf> example = buildGlobalSearchSpec(Shelf.class, paginationDto.getKeyword());
            if (example != null) {
                return shelfService.findAll(example, pageable);
            }
        }
        return shelfService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ShelfDto add(@RequestBody ShelfDto shelfDto) {
        return shelfService.save(shelfDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ShelfDto get(@RequestParam String identifier) {
        return shelfService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ShelfDto update(@RequestBody ShelfDto shelfDto) {
        return shelfService.update(shelfDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public boolean delete(@RequestBody ShelfDto shelfDto) {
        try {
            shelfService.delete(shelfDto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/toggleStatus")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ShelfDto toggleStatus(@RequestBody ShelfDto shelfDto) {
        return shelfService.toggleStatus(shelfDto.getIdentifier(), shelfDto.isStatus());
    }

    @GetMapping("/active")
    public List<ShelfDto> activeShelves() {
        return shelfService.findActiveShelves();
    }
}