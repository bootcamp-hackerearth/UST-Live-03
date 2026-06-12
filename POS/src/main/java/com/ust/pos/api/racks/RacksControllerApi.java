package com.ust.pos.api.racks;
import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.racks.service.RacksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("api/racks")
public class RacksControllerApi extends BaseController {

    @Autowired
    private RacksService racksService;

    @PostMapping("/list")
    public PageDto<RacksDto> racks(@RequestBody PaginationDto paginationDto) {
        Pageable pageable=getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),paginationDto.getSortDirection(),paginationDto.getSortField());
        return racksService.findAll(pageable);
    }

    @GetMapping("/identifier")
    public RacksDto getRacksByIdentifier(@RequestParam String identifier) {
        return racksService.findByIdentifier(identifier);
    }

    @PostMapping("/add")
    public RacksDto addPost(@RequestBody RacksDto racksDto) {
        return racksService.save(racksDto);
    }
    @PostMapping("/update")
    public RacksDto updatePost(@RequestBody RacksDto racksDto) {
        return racksService.update(racksDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            racksService.delete(identifier);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }
    @GetMapping("/toggleStatus")
    public void toggleStatus(@RequestParam String identifier) {
        racksService.toggleStatus(identifier);
    }
}
