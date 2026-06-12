package com.ust.pos.api.racks;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.racks.service.RacksService;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/rack")
public class ApiRacksController extends BaseController {

    public static final String REDIRECT_LIST = "redirect:/rack/list";

    @Autowired
    private RacksService racksService;

    @Autowired
    private ShelfService shelfService;

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

    @PostMapping("/update")
    public RacksDto update(@RequestBody RacksDto rackDto) {
        return racksService.update(rackDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            racksService.delete(identifier);
        } catch (Exception e) {
            return true;
        }
        return false;
    }

}