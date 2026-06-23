package com.ust.pos.api.unit;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.unit.service.UnitService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("unitApiController")
@RequestMapping("/api/units")
public class UnitController extends BaseController {
    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @PostMapping("/list")
    public WsDto<UnitDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return unitService.findAll(pageable);
    }

    @GetMapping("/active")
    public ResponseEntity<List<UnitDto>> getActiveUnits() {
        List<UnitDto> activeUnits = unitService.findIfTrue();
        return ResponseEntity.ok(activeUnits);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<UnitDto> getByIdentifier(@PathVariable String identifier) {
        UnitDto response = unitService.findByIdentifier(identifier);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<UnitDto> save(@RequestBody UnitDto unitDto) {
        UnitDto response = unitService.save(unitDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{identifier}")
    public ResponseEntity<UnitDto> update(@PathVariable String identifier, @RequestBody UnitDto unitDto) {
        unitDto.setIdentifier(identifier);
        UnitDto response = unitService.update(unitDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{identifier}")
    public ResponseEntity<Boolean> delete(@PathVariable String identifier) {
        try {
            unitService.delete(identifier);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @PostMapping("/toggle/{identifier}")
    public ResponseEntity<UnitDto> toggleStatus(@PathVariable String identifier) {
        UnitDto response = unitService.toggleStatus(identifier);
        return ResponseEntity.ok(response);
    }
}