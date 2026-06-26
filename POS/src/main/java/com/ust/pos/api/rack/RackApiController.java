package com.ust.pos.api.rack;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.RackDto;
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

    @PostMapping("/add")
    public RackDto addPost(@RequestBody RackDto rackDto) {
        return rackService.save(rackDto);
    }

    @PostMapping("/list")
    public PaginationResponseDto<RackDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),paginationDto.getSortField());
        return rackService.findAll(pageable);
    }

    @GetMapping("/get")
    public RackDto update(@RequestParam String identifier) {
        return rackService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public RackDto updatePost(@RequestBody RackDto rackDto) {
        return rackService.update(rackDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            rackService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    public RackDto toggle(@RequestBody RackDto dto) {
        return rackService.toggleStatus(dto.getIdentifier(), dto.isStatus());
    }

    @GetMapping("/rackstatus")
    public List<RackDto> findActive() {
        return rackService.findActiveRacks();
    }
}