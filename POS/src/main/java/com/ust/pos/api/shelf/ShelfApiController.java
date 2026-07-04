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
    public WsDto<ShelfDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getSearch())) {
            Specification<Shelf> example = buildGlobalSearchSpec(Shelf.class, paginationDto.getSearch());
            if (example != null) {
                return shelfService.findAll(example, pageable);
            }
        }
        return shelfService.findAll(pageable);
    }

    @PostMapping("/add")
    public ShelfDto addPost(@RequestBody ShelfDto userDto) {

        return shelfService.save(userDto);
    }

    @GetMapping("/{identifier}")
    public ShelfDto update(@PathVariable String identifier) {

        return shelfService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ShelfDto updatePost(@RequestBody ShelfDto shelfDto) {

        return shelfService.update(shelfDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody ShelfDto shelfDto) {

        String identifier = shelfDto.getIdentifier();

        try {
            shelfService.delete(identifier);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @PatchMapping("/toggle")
    public boolean toggle(@RequestBody String identifier) {

        try {
            shelfService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @PostMapping("/getactive")
    public List<ShelfDto> getActiveShelves() {

        return shelfService.findActiveShelf();
    }
}
