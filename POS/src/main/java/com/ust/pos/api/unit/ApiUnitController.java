package com.ust.pos.api.unit;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.unit.service.UnitService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/unit")
public class ApiUnitController extends BaseController {

    private final UnitService unitService;

    public ApiUnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @PostMapping("/list")
    public WsDto<UnitDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return unitService.findAll(pageable);
    }

    @PostMapping("/add")
    public UnitDto addunit(@RequestBody UnitDto unitDto) {
        return unitService.save(unitDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try{
            unitService.delete(identifier);
        }
        catch (Exception e){
            return false;
        }
        return true;
    }

    @GetMapping("/get")
    public UnitDto update(@RequestParam String identifier) {
        return unitService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public UnitDto updatePrice(@RequestBody UnitDto unitDto) {
       return unitService.update(unitDto);
    }

    @PostMapping("/toggle")
    public UnitDto toggle(@RequestBody UnitDto unitDto) {
        return unitService.changeToggleStatus(unitDto.getIdentifier(), unitDto.isStatus());
    }

    @PostMapping("/findActiveStatus")
    public List<UnitDto> findActive() {
        return unitService.findActiveStatus();
    }
}
