package com.ust.pos.api.shelf;

import com.ust.pos.base.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.shelf.service.ShelfService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shelf")
public class ShelfApiController extends BaseController {

    private final ShelfService shelfService;

    @PostMapping("/list")
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
    public ShelfDto addPost(@RequestBody ShelfDto shelfDto) {
        return shelfService.save(shelfDto);
    }

    @GetMapping("/get")
    public ShelfDto get(@RequestParam String identifier) {
        return shelfService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ShelfDto update(@RequestBody ShelfDto shelfDto) {
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

    @PatchMapping("/toggle")
    public ShelfDto toggle(@RequestParam String identifier) {
        return shelfService.toggleStatus(identifier);
    }

    @GetMapping("/active")
    public List<ShelfDto> findActiveShelves() {
        return shelfService.findActiveShelves();
    }

}