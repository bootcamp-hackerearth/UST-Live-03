package com.ust.pos.api.rack;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.rack.service.RackService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rack")
public class RackApiController extends BaseController {
    private final RackService rackService;

    public RackApiController(RackService rackService) {
        this.rackService = rackService;
    }

    @PostMapping("/list")
    public WsDto<RackDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return rackService.findAll(pageable);
    }

    @PostMapping("/add")
    public RackDto add(@RequestBody RackDto rackDto) {
        return rackService.save(rackDto);
    }

    @GetMapping("/get")
    public RackDto get(@RequestParam String identifier) {
        return rackService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public RackDto update(@RequestBody RackDto rackDto) {
        return rackService.update(rackDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody RackDto rackDto) {
        try {
            rackService.delete(rackDto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/toggleStatus")
    public RackDto toggleStatus(@RequestBody RackDto rackDto) {
        return rackService.toggleStatus(rackDto.getIdentifier(), rackDto.isStatus());
    }

    @GetMapping("/active")
    public List<RackDto> getActiveRacks() {
        return rackService.findActiveRacks();
    }
}