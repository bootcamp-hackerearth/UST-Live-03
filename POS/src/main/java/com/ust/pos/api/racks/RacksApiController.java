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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/racks")
public class RacksApiController extends BaseController {

    private final RacksService racksService;

    public RacksApiController(RacksService racksService) {
        this.racksService = racksService;
    }

    @PostMapping("/list")
    public WsDto<RacksDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getSearch())) {
            Specification<Racks> example = buildGlobalSearchSpec(Racks.class, paginationDto.getSearch());
            if (example != null) {
                return racksService.findAll(example, pageable);
            }
        }
        return racksService.findAll(pageable);
    }

    @PostMapping("/add")
    public RacksDto addPost(@RequestBody RacksDto userDto) {

        return racksService.save(userDto);
    }

    @GetMapping("/{identifier}")
    public RacksDto update(@PathVariable String identifier) {

        return racksService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public RacksDto updatePost(@RequestBody RacksDto racksDto) {

        return racksService.update(racksDto);
    }

    @PatchMapping("/toggle")
    public boolean toggleStatus(@RequestBody String identifier) {

        try {
            racksService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody RacksDto racksDto) {

        String identifier = racksDto.getIdentifier();

        try {
            racksService.delete(identifier);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
