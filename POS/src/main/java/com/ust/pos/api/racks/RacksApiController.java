package com.ust.pos.api.racks;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Racks;
import com.ust.pos.racks.service.RacksService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/racks")
@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'STOCK_MANAGER')")
public class RacksApiController extends BaseController {
    private final RacksService racksService;

    public RacksApiController(RacksService racksService) {
        this.racksService = racksService;
    }

    @PostMapping("/list")
    public WsDto<RacksDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Racks> example = buildGlobalSearchSpec(Racks.class, paginationDto.getKeyword());
            if (example != null) {
                return racksService.findAll(example, pageable);
            }
        }

        return racksService.findAll(pageable);
    }

    @PostMapping("/add")
    public RacksDto addPost(@RequestBody RacksDto racksDto) {
        return racksService.save(racksDto);
    }

    @GetMapping("/get")
    public RacksDto update(@RequestParam String identifier) {
        return racksService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public RacksDto updatePost(@RequestBody RacksDto racksDto) {
        return racksService.update(racksDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            racksService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    public RacksDto toggle(@RequestParam String identifier) {
        return racksService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<RacksDto> findByStatus() {
        return racksService.findIfTrue();
    }
}