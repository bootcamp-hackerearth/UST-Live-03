package com.ust.pos.api.unit;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.unit.service.UnitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

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
                paginationDto.getSortField());
        Page<UnitDto> unit = unitService.findAll(pageable, paginationDto.getSearch());
        WsDto<UnitDto> result = new WsDto<>();
        result.setContent(unit.getContent());
        result.setSizePerPage(unit.getSize());
        result.setPage(unit.getNumber());
        result.setTotalPages(unit.getTotalPages());
        return result;
    }

    @PostMapping("/add")
    public UnitDto addModel(@RequestBody UnitDto unitDto) {
        return unitService.save(unitDto);
    }

    @GetMapping("/get")
    public UnitDto update(@RequestParam String identifier) {
        return unitService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public UnitDto updatePost(@RequestBody UnitDto unitDto) {
        return unitService.update(unitDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            unitService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    public boolean updateStatus(
            @RequestParam String identifier,
            @RequestParam Boolean status) {
        try {
            unitService.updateStatusOnly(identifier, status);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}