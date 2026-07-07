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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelf")
public class ShelfControllerApi extends BaseController {
    public static final String REDIRECT_SHELF_LIST = "redirect:/shelf/list";

    private final ShelfService shelfService;

    public ShelfControllerApi(ShelfService shelfService) {
        this.shelfService = shelfService;
    }

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
    public ShelfDto update(@RequestParam String identifier) {
        return shelfService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ShelfDto updatePost(@RequestBody ShelfDto shelfDto) {
        return shelfService.update(shelfDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            shelfService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PatchMapping("/toggle")
    public boolean toggle(@RequestParam String identifier, boolean status) {
        try {
            shelfService.updateStatus(identifier, status);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/list-active")
    public List<ShelfDto> listActive() {
        return shelfService.findAllActive();
    }
}