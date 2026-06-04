package com.ust.pos.api.unit;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UnitDto;
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
    public List<UnitDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(), paginationDto.getSortField());
        return unitService.findAll(pageable);
    }


    @PostMapping("/add")
    public UnitDto addModel(@RequestBody UnitDto unitDto) {
        return unitService.save(unitDto);
    }

    @GetMapping("/get")
    public UnitDto update(@RequestParam String identifier) {
        return unitService.findByIdentifier(identifier);

    }

    @PostMapping("/update")
    public UnitDto updatePost(@RequestBody UnitDto unitDto) {
        return unitService.update(unitDto);

    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            unitService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    public boolean toggleStatus(@RequestParam String identifier) {
        try {
            unitService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}