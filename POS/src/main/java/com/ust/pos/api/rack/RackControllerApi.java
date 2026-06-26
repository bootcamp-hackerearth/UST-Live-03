package com.ust.pos.api.rack;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RackDto;
import com.ust.pos.rack.service.RackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rack")
@RequiredArgsConstructor
public class RackControllerApi extends BaseController {

    private final RackService rackService;

    @PostMapping("/list")
    public PaginatedResponseDto<RackDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return rackService.findAll(pageable);
    }

    @PostMapping("/add")
    public RackDto addPost(@RequestBody RackDto rackDto) {
        return rackService.save(rackDto);
    }

    @GetMapping("/get")
    public RackDto get(@RequestParam String identifier) {
        return rackService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public RackDto updatePost(@RequestBody RackDto rackDto) {
        return rackService.update(rackDto);
    }

    @DeleteMapping("/delete")
    public RackDto delete(@RequestBody RackDto rackDto) {
        try {
            return rackService.delete(rackDto.getIdentifier());
        } catch (Exception e) {
            RackDto errorDto = new RackDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Delete failed: " + e.getMessage());
            return errorDto;
        }
    }

    @PatchMapping("/toggle")
    public boolean changeStatus(@RequestBody RackDto rackDto) {
        try {
            rackService.changeStatus(rackDto.getIdentifier(), rackDto.getStatus());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}