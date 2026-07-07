package com.ust.pos.api.racks;
import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.model.Racks;
import com.ust.pos.racks.service.RacksService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("api/racks")
public class RacksControllerApi extends BaseController {

    private final RacksService racksService;

    public RacksControllerApi(RacksService racksService){
        this.racksService=racksService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin')")
    public PageDto<RacksDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Racks> spec = buildGlobalSearchSpec(Racks.class, paginationDto.getKeyword());
            return racksService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return racksService.findAll(pageable);
    }

    @GetMapping("/identifier")
    public RacksDto getRacksByIdentifier(@RequestParam String identifier) {
        return racksService.findByIdentifier(identifier);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin')")
    public RacksDto addPost(@RequestBody RacksDto racksDto) {
        return racksService.save(racksDto);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Admin')")
    public RacksDto updatePost(@RequestBody RacksDto racksDto) {
        return racksService.update(racksDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Admin')")
    public boolean delete(@RequestParam String identifier) {
        try {
            racksService.delete(identifier);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    @PreAuthorize("hasAuthority('Admin')")
    public void toggleStatus(@RequestParam String identifier) {
        racksService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<RacksDto> findByStatus() {
        return racksService.findActiveRacks();
    }
}
