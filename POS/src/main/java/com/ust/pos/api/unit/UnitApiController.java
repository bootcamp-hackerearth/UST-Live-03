package com.ust.pos.api.unit;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.unit.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unit")
public class UnitApiController extends BaseController {

    @Autowired
    private UnitService unitService;

    @PostMapping("/list")
    public WsDto<UnitDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
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

    @PostMapping("/update")
    public UnitDto update(@RequestBody UnitDto unitDto) {
        return unitService.update(unitDto);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody UnitDto unitDto) {
        try {
            unitService.delete(unitDto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/unit")
    public List<UnitDto> getActiveUnit() {
        return unitService.findActiveUnit();
    }

    @PostMapping("/toggleStatus")
    public UnitDto toggleStatus(@RequestBody UnitDto unitDto) {
        return unitService.toggleStatus(unitDto.getIdentifier(), unitDto.isStatus());
    }
}