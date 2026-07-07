package com.ust.pos.api.racks;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Racks;
import com.ust.pos.racks.service.RacksService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/racks")
@PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
public class RacksApiController extends BaseController {

    private final RacksService racksService;

    public RacksApiController(RacksService racksService) {
        this.racksService = racksService;
    }

    @PostMapping("/list")
    public WsDto<RacksDto> home(@RequestBody PaginationDto paginationDto) throws Exception {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        Example<Racks> example = buildSearchProbe(Racks.class, paginationDto.getSearch());
        Page<RacksDto> pageResult = racksService.findAll(example, pageable);

        WsDto<RacksDto> response = new WsDto<>();

        response.setDtoList(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPage(pageResult.getTotalPages());
        response.setTotalRecords(pageResult.getTotalElements());

        return response;
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
    public Boolean delete(@RequestParam String identifier) {
        try {
            racksService.delete(identifier);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    @GetMapping("/toggleStatus")
    public Boolean toggleStatus(@RequestParam String identifier) {
        try {
            racksService.toggleStatus(identifier);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

}