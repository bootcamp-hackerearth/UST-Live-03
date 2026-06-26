package com.ust.pos.api.racks;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.racks.service.RacksService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/racks")
public class RacksApiController extends BaseController {

    private final RacksService racksService;

    public RacksApiController(RacksService racksService) {
        this.racksService = racksService;
    }

    @GetMapping("/list")
    public List<RacksDto> home() {
        return racksService.findAll();
    }

    @PostMapping("/list")
    public WsDto<RacksDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),paginationDto.getSortField());
        Page<RacksDto> pageResult = racksService.findAll(pageable, paginationDto.getSearch());
        WsDto<RacksDto> response = new WsDto<>();
        response.setContent(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPages(pageResult.getTotalPages());
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
    public boolean delete(@RequestParam String identifier) {
        try {
            racksService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    public boolean toggleStatus(@RequestBody String identifier) {
        try {
            racksService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
