package com.ust.pos.api.rack;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.rack.service.RackService;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/rack")
@RestController
public class RackApiController extends BaseController {

    @Autowired
    private RackService rackService;
    @Autowired
    private ShelfService shelfService;

    @PostMapping("/list")
    public WsDto<RackDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return rackService.findAll(pageable);
    }

    @PostMapping("/add")
    public RackDto addPost(@RequestBody RackDto rackDto) {
        return rackService.save(rackDto);
    }

    @GetMapping("/get")
    public RackDto update(@RequestParam String identifier) {
        return rackService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public RackDto updatePost(@RequestBody RackDto rackDto) {
        return rackService.update(rackDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            rackService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/toggle")
    public RackDto toggle(@RequestParam String identifier) {
        return rackService.toggleStatus(identifier);
    }

    @GetMapping("/active")
    public List<RackDto> findActiveRacksDto() {
        return rackService.findActiveRacks();
    }

}
