package com.ust.pos.api.racks;

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

@RestController
@RequestMapping("/api/racks")
public class RacksApiController extends BaseController {

    private final RacksService racksService;

    public RacksApiController(RacksService racksService) {
        this.racksService = racksService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public WsDto<RacksDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Racks> example = buildGlobalSearchSpec(Racks.class, paginationDto.getKeyword());
            if (example != null) {
                return racksService.findAll(example, pageable, paginationDto.getKeyword());
            }
        }
        return racksService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public RacksDto addPost(@RequestBody RacksDto racksDto) {
        return racksService.save(racksDto);
    }

    @PostMapping("/get")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public RacksDto update(@RequestBody String identifier) {
        return racksService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public RacksDto updatePost(@RequestBody RacksDto racksDto) {
        return racksService.update(racksDto);

    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public boolean delete(@RequestBody String identifier) {
        try {
            racksService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public String toggleStatus(@RequestBody String identifier) {
        racksService.toggleStatus(identifier);
        return identifier;
    }
}