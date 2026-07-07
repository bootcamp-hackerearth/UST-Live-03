package com.ust.pos.api.shelf;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.shelf.service.ShelfService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelf")
@RequiredArgsConstructor
public class ShelfControllerApi extends BaseController {

    private final ShelfService shelfService;

    @PostMapping("/list")
    public PaginatedResponseDto<ShelfDto> list(@RequestBody PaginationDto paginationDto) {
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
    public ShelfDto updatePost(@RequestBody ShelfDto shelfDto) {
        return shelfService.update(shelfDto);
    }

    @DeleteMapping("/delete")
    public ShelfDto delete(@RequestBody ShelfDto shelfDto) {
        try {
            return shelfService.delete(shelfDto.getIdentifier());
        } catch (Exception e) {
            ShelfDto errorDto = new ShelfDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Delete failed: " + e.getMessage());
            return errorDto;
        }
    }

    @GetMapping("/active")
    public List<ShelfDto> findAllActive() {
        return shelfService.findAllActive();
    }

    @PatchMapping("/toggle")
    public boolean changeStatus(@RequestBody ShelfDto shelfDto) {
        try {
            shelfService.changeStatus(shelfDto.getIdentifier(), shelfDto.getStatus());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}