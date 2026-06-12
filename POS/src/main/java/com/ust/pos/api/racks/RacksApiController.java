package com.ust.pos.api.racks;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.racks.service.RacksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/racks")
public class RacksApiController extends BaseController {

    @Autowired
    private RacksService racksService;

    @PostMapping("/list")
    public WsDto<RacksDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return racksService.findAll(pageable);
    }

    @PostMapping("/add")
    public RacksDto addPost(@RequestBody RacksDto racksDto) {
        return racksService.save(racksDto);
    }

    @PostMapping("/get")
    public RacksDto update(@RequestBody String identifier) {
        return racksService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public RacksDto updatePost(@RequestBody RacksDto racksDto) {
        return racksService.update(racksDto);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody String identifier) {
        try {
            racksService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
