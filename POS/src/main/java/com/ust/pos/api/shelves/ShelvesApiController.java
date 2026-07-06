package com.ust.pos.api.shelves;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelvesDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelves;
import com.ust.pos.shelves.service.ShelvesService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shelves")
public class ShelvesApiController extends BaseController {

    private final ShelvesService shelvesService;

    public ShelvesApiController(ShelvesService shelvesService) {
        this.shelvesService = shelvesService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public WsDto<ShelvesDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Shelves> example = buildGlobalSearchSpec(Shelves.class, paginationDto.getKeyword());
            if (example != null) {
                return shelvesService.findAll(example, pageable, paginationDto.getKeyword());
            }
        }
        return shelvesService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public ShelvesDto addPost(@RequestBody ShelvesDto shelvesDto) {
        return shelvesService.save(shelvesDto);
    }

    @PostMapping("/get")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public ShelvesDto update(@RequestBody String identifier) {
        return shelvesService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public ShelvesDto updatePost(@RequestBody ShelvesDto shelvesDto) {
        return shelvesService.update(shelvesDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public boolean delete(@RequestBody String identifier) {
        try {
            shelvesService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public String toggleStatus(@RequestBody String identifier) {
        shelvesService.toggleStatus(identifier);
        return identifier;
    }
}