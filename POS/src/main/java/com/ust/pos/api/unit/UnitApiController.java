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
    public WsDto<UnitDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getSearch())) {
            Specification<Unit> example = buildGlobalSearchSpec(Unit.class, paginationDto.getSearch());
            if (example != null) {
                return unitService.findAll(example, pageable);
            }
        }
        return unitService.findAll(pageable);
    }

    @PostMapping("/add")
    public UnitDto addPost(@RequestBody UnitDto unitDto) {

        return unitService.save(unitDto);
    }

    @GetMapping("/{identifier}")
    public UnitDto update(@PathVariable String identifier) {

        return unitService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public UnitDto updatePost(@RequestBody UnitDto unitDto) {

        return unitService.update(unitDto);
    }

    @PatchMapping("/toggle")
    public boolean toggleStatus(@RequestBody String identifier) {

        try {
            unitService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/getactive")
    public List<UnitDto> getActiveUnits() {

        return unitService.findActiveUnit();
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody UnitDto unitDto) {

        String identifier = unitDto.getIdentifier();

        try {
            unitService.delete(identifier);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}

