package com.ust.pos.api.unit;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.unit.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unit")
@RequiredArgsConstructor
public class UnitControllerApi extends BaseController {

    private final UnitService unitService;

    @PostMapping("/list")
    public PaginatedResponseDto<UnitDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return unitService.findAll(pageable);
    }

    @PostMapping("/add")
    public UnitDto addPost(@RequestBody UnitDto unitDto) {
        return unitService.save(unitDto);
    }

    @GetMapping("/get")
    public UnitDto get(@RequestParam String identifier) {
        return unitService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public UnitDto updatePost(@RequestBody UnitDto unitDto) {
        return unitService.update(unitDto);
    }

    @DeleteMapping("/delete")
    public UnitDto delete(@RequestBody UnitDto unitDto) {
        try {
            return unitService.delete(unitDto.getIdentifier());
        } catch (Exception e) {
            UnitDto errorDto = new UnitDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Delete failed: " + e.getMessage());
            return errorDto;
        }
    }

    @PatchMapping("/toggle")
    public boolean changeStatus(@RequestBody UnitDto unitDto) {
        try {
            unitService.changeStatus(unitDto.getIdentifier(), unitDto.getStatus());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/active")
    public List<UnitDto> getActiveUnits() {
        return unitService.findAllActive();
    }
}