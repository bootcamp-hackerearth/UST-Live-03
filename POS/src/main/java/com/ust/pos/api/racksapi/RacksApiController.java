package com.ust.pos.api.racksapi;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Racks;
import com.ust.pos.racks.service.RacksService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/racks")
public class RacksApiController extends BaseController {

    public RacksApiController(RacksService racksService) {
        this.racksService = racksService;
    }

    private final RacksService racksService;

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN','ACCOUNTANT')")
    public WsDto<RacksDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Racks> example = buildGlobalSearchSpec(Racks.class, paginationDto.getKeyword());
            if (example != null) {
                return racksService.findAll(example, pageable, paginationDto.getKeyword());
            }
        }
        return racksService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN')")
    public RacksDto addPost(@RequestBody RacksDto racksDto) {
        return racksService.save(racksDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN')")
    public RacksDto update(@RequestParam String identifier) {
        return racksService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN')")
    public RacksDto updatePost(@RequestBody RacksDto racksDto) {
        return racksService.update(racksDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN')")
    public boolean delete(@RequestParam String identifier) {
        try {
            racksService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN')")
    public RacksDto toggle(@RequestParam String identifier) {
        return racksService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN')")
    public List<RacksDto> findByStatus() {
        return racksService.findIfTrue();
    }
}