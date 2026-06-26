package com.ust.pos.api.racks;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.racks.service.RacksService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/racks")
@RequiredArgsConstructor
public class ApiRacksController extends BaseController {

    private final RacksService racksService;

    @PostMapping("/list")
    public WsDto<RacksDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return racksService.findAll(pageable);
    }

    @PostMapping("/add")
    public RacksDto addPost(@RequestBody RacksDto rackDto) {
        return racksService.save(rackDto);
    }

    @GetMapping("/get")
    public RacksDto get(@RequestParam String identifier) {
        return racksService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public RacksDto update(@RequestBody RacksDto rackDto) {
        return racksService.update(rackDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            racksService.delete(identifier);
        } catch (Exception exception) {
            return true;
        }
        return false;
    }

    @GetMapping("/findallactive")
    public List<RacksDto> findAllActive() {
        return racksService.findAllActive();
    }

    @PatchMapping("/toggle-status")
    public RacksDto toggle(@RequestParam String identifier) {
        return racksService.toggleStatus(identifier);
    }

}