package com.ust.pos.api.shelfs;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelfs;
import com.ust.pos.shelfs.service.ShelfsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelfs")
@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'STOCK_MANAGER')")
public class ShelfsApiController extends BaseController {

    private final ShelfsService shelfsService;

    public ShelfsApiController(ShelfsService shelfsService) {
        this.shelfsService = shelfsService;
    }

    @PostMapping("/list")
    public WsDto<ShelfsDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Shelfs> example = buildGlobalSearchSpec(Shelfs.class, paginationDto.getKeyword());
            if (example != null) {
                return shelfsService.findAll(example, pageable);
            }
        }

        return shelfsService.findAll(pageable);
    }

    @PostMapping("/add")
    public ShelfsDto addPost(@RequestBody ShelfsDto shelfsDto) {
        return shelfsService.save(shelfsDto);
    }

    @GetMapping("/get")
    public ShelfsDto update(@RequestParam String identifier) {
        return shelfsService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ShelfsDto updatePost(@RequestBody ShelfsDto shelfsDto) {
        return shelfsService.update(shelfsDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            shelfsService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("toggle-status")
    public ShelfsDto toggle(@RequestParam String identifier) {
        return shelfsService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<ShelfsDto> findByStatus() {
        return shelfsService.findIfTrue();
    }
}