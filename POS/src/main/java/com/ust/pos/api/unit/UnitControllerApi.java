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
public class UnitControllerApi extends BaseController {

    @Autowired
    private UnitService unitService;

    @PostMapping("/list")
    public WsDto<UnitDto> list(@RequestBody PaginationDto pagination) {
        Pageable pageable = getPageable(pagination.getPage(), pagination.getSizePerPage(),
                pagination.getSortDirection(), pagination.getSortfield());
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
    public UnitDto delete(@RequestBody UnitDto unitDto) {
        UnitDto response = new UnitDto();
        try {
            unitService.delete(unitDto.getIdentifier());
            response.setSuccess(true);
            response.setMessage("Unit deleted successfully");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Delete failed");
        }
        return response;
    }

    @PostMapping("/toggle")
    public UnitDto toggle(@RequestBody UnitDto unitDto) {
        return unitService.toggleStatus(unitDto.getIdentifier());
    }

    @PostMapping("/active")
    public List<UnitDto> active() {
        return unitService.findActiveUnits();
    }
}