package com.ust.pos.api.racks;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.racks.service.RacksService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public WsDto<RacksDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortField()
        );

        Page<RacksDto> pageResult =
                racksService.findAll(
                        pageable,
                        paginationDto.getSearch()
                );

        WsDto<RacksDto> output = new WsDto<>();
        output.setContent(pageResult.getContent());
        output.setPage(pageResult.getNumber());
        output.setSizePerPage(pageResult.getSize());
        output.setTotalPages(pageResult.getTotalPages());

        return output;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
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
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public boolean delete(@RequestParam String identifier) {
        try {
            racksService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public boolean toggleStatus(@RequestParam String identifier) {
        try {
            racksService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}