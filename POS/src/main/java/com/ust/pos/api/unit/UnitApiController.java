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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unit")
public class UnitApiController extends BaseController {
    private final UnitService unitService;

    public UnitApiController(UnitService unitService) {
        this.unitService = unitService;
    }

    @PostMapping("/list")
    public WsDto<UnitDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Unit> example = buildGlobalSearchSpec(Unit.class, paginationDto.getKeyword());
            if (example != null) {
                return unitService.findAll(example, pageable);
            }
        }
        return unitService.findAll(pageable);
    }

    @PostMapping("/add")
    public UnitDto add(@RequestBody UnitDto unitDto) {
        return unitService.save(unitDto);
    }

    @GetMapping("/get")
    public UnitDto get(@RequestParam String identifier) {
        return unitService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public UnitDto update(@RequestBody UnitDto unitDto) {
        return unitService.update(unitDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody UnitDto unitDto) {
        try {
            unitService.delete(unitDto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/active")
    public List<UnitDto> getActiveUnit() {
        return unitService.findActiveUnit();
    }

    @PostMapping("/toggleStatus")
    public UnitDto toggleStatus(@RequestBody UnitDto unitDto) {
        return unitService.toggleStatus(unitDto.getIdentifier(), unitDto.isStatus());
    }
}