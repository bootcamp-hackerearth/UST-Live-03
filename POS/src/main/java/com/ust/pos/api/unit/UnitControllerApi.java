package com.ust.pos.api.unit;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.unit.service.UnitService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unit")
public class UnitControllerApi extends BaseController {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UnitService unitService;

    @PostMapping("/list")
    public WsDto<UnitDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return unitService.findAll(pageable);
    }

    @PostMapping("/add")
    public UnitDto addPost(@RequestBody UnitDto unitDto) {
        return unitService.save(unitDto);
    }

    @GetMapping("/get")
    public UnitDto update(@RequestParam String identifier, @ModelAttribute UnitDto unitDto) {
        return unitService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public UnitDto updatePost(@ModelAttribute UnitDto unitDto) {
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

    @GetMapping("/active")
    public List<Unit> findActiveUnit() {
        return unitService.findActiveUnits();
    }

    @GetMapping("/toggle")
    public UnitDto toggle(@RequestParam String identifier) {
        return unitService.toggleStatus(identifier);
    }
}
