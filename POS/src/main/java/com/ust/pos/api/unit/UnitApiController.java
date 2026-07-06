package com.ust.pos.api.unit;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.unit.service.UnitService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/unit")
public class UnitApiController extends BaseController {

    private final UnitService unitService;

    public UnitApiController(UnitService unitService) {
        this.unitService = unitService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public WsDto<UnitDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Unit> example = buildGlobalSearchSpec(Unit.class, paginationDto.getKeyword());
            if (example != null) {
                return unitService.findAll(example, pageable, paginationDto.getKeyword());
            }
        }
        return unitService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public UnitDto addPost(@RequestBody UnitDto unitDto) {
        return unitService.save(unitDto);

    }

    @PostMapping("/get")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public UnitDto update(@RequestBody String identifier) {
        return unitService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Developer','Tester','Admin','HackerEarth')")
    public UnitDto updatePost(@RequestBody UnitDto unitDto) {
        return unitService.update(unitDto);

    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public boolean delete(@RequestBody String identifier) {
        try {
            unitService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('Developer','Tester','Admin','HackerEarth')")
    public String toggleStatus(@RequestBody String identifier) {
        unitService.toggleStatus(identifier);
        return identifier;
    }
}