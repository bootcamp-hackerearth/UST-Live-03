package com.ust.pos.api.shelfs;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelfs;
import com.ust.pos.shelfs.sevice.ShelfsService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/shelfs")
public class ApiShelfsController extends BaseController {

    private final ShelfsService shelfsService;

    public ApiShelfsController(ShelfsService shelfsService) {
        this.shelfsService = shelfsService;
    }

    @PostMapping("/list")
    public WsDto<ShelfsDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable= getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortField(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Shelfs> example = buildGlobalSearchSpec(Shelfs.class, paginationDto.getKeyword());
            if (example != null) {
                return shelfsService.findAll(example, pageable);
            }
        }
        return shelfsService.findAll(pageable);
    }

    @PostMapping("/add")
    public ShelfsDto addshelfs(@RequestBody ShelfsDto shelfsDto) {
        return shelfsService.save(shelfsDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try{
            shelfsService.delete(identifier);
        }
        catch (Exception e){
            return false;
        }
        return true;
    }

    @GetMapping("/get")
    public ShelfsDto update(@RequestParam String identifier) {
        return shelfsService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ShelfsDto updatePrice(@RequestBody ShelfsDto shelfsDto) {
        return shelfsService.update(shelfsDto);
    }

    @PostMapping("/toggle")
    public ShelfsDto toggle(@RequestBody ShelfsDto shelfsDto) {
        return shelfsService.changeToggleStatus(shelfsDto.getIdentifier(), shelfsDto.isStatus());
    }
}
