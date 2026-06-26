package com.ust.pos.api.racks;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.racks.service.RacksService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/racks")
public class RacksApiController extends BaseController {

    public static final String REDIRECT_RACKS_LIST = "redirect:/racks/list";

    private final RacksService racksService;

    public RacksApiController(RacksService racksService) {
        this.racksService = racksService;
    }

    @PostMapping("/list")
    public List<RacksDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortField());
        return racksService.findAll(pageable);
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
    public boolean toggleStatus(@RequestParam String identifier) {
        try {
            racksService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}