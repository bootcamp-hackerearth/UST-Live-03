package com.ust.pos.api.racks;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.racks.service.RacksService;
import com.ust.pos.shelfs.sevice.ShelfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/rack")
public class ApiRacksController extends BaseController {

    @Autowired
    RacksService racksService;

    @Autowired
    ShelfsService shelfsService;

    @PostMapping("/list")
    public WsDto<RacksDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable=getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return racksService.findAll(pageable);
    }

    @PostMapping("/add")
    public RacksDto addracks(@RequestBody RacksDto racksDto) {
        return racksService.save(racksDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            racksService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/get")
    public RacksDto update(@RequestParam String identifier) {
        return racksService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public RacksDto updatePrice(@RequestBody RacksDto racksDto) {
        return racksService.update(racksDto);
    }

    @PostMapping("/toggle")
    public RacksDto toggle(@RequestBody RacksDto racksDto) {
        return racksService.changeToggleStatus(racksDto.getIdentifier(), racksDto.isStatus());
    }

    @PostMapping("/findActiveStatus")
    public List<RacksDto> findActive() {
        return racksService.findActiveStatus();
    }
}
