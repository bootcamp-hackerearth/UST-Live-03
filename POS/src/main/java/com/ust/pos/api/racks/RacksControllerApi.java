package com.ust.pos.api.racks;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Racks;
import com.ust.pos.racks.service.RacksService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/racks")
public class RacksControllerApi extends BaseController {

    private final RacksService racksService;

    public RacksControllerApi(RacksService racksService) {
        this.racksService = racksService;
    }

    @PostMapping("/list")
    public WsDto<RacksDto> listCategories(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Racks> example = buildGlobalSearchSpec(Racks.class, paginationDto.getKeyword());
            if (example != null) {
                return racksService.findAll(example, pageable);
            }
        }
        return racksService.findAll(pageable);
    }

    @PostMapping("/add")
    public RacksDto saveRacks(@RequestBody RacksDto racksDto) {

        return racksService.save(racksDto);

    }


    @GetMapping("/update")
    public RacksDto showEditPage(@RequestParam Long id) {

        return racksService.findById(id);
    }

    @PutMapping("/update")
    public RacksDto saveEditedRacks(@RequestBody RacksDto racksDto) {

        return racksService.update(racksDto);

    }


    @DeleteMapping("/delete")
    public boolean deleteRacks(@RequestParam String identifier) {

        try {
            racksService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/changeStatus")
    public RacksDto toggle(@RequestBody RacksDto racksDto) {
        return racksService.changeRacksStatus(racksDto.getIdentifier(), racksDto.isStatus());
    }
}