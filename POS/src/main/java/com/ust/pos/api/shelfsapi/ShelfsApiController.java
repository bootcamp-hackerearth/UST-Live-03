package com.ust.pos.api.shelfsapi;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelfs;
import com.ust.pos.shelfs.service.ShelfsService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelfs")
public class ShelfsApiController extends BaseController {

    private final ShelfsService shelfsService;

    public ShelfsApiController(ShelfsService shelfsService) {
        this.shelfsService = shelfsService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN','ACCOUNTANT')")
    public WsDto<ShelfsDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Shelfs> example = buildGlobalSearchSpec(Shelfs.class, paginationDto.getKeyword());
            if (example != null) {
                return shelfsService.findAll(example, pageable, paginationDto.getKeyword());
            }
        }
        return shelfsService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN')")
    public ShelfsDto addPost(@RequestBody ShelfsDto shelfsDto) {
        return shelfsService.save(shelfsDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN','ACCOUNTANT')")
    public ShelfsDto update(@RequestParam String identifier) {
        return shelfsService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN')")
    public ShelfsDto updatePost(@RequestBody ShelfsDto shelfsDto) {
        return shelfsService.update(shelfsDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN')")
    public boolean delete(@RequestParam String identifier) {
        try {
            shelfsService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("toggle-status")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN')")
    public ShelfsDto toggle(@RequestParam String identifier) {
        return shelfsService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN','ACCOUNTANT')")
    public List<ShelfsDto> findByStatus() {
        return shelfsService.findIfTrue();
    }
}