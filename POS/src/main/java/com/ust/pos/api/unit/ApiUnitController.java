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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public WsDto<UnitDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Unit> example = buildGlobalSearchSpec(Unit.class, paginationDto.getKeyword());
            if (example != null) {
                return unitService.findAll(example, pageable);
            }
        }
        return unitService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public UnitDto addunit(@RequestBody UnitDto unitDto) {
        return unitService.save(unitDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
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
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public UnitDto update(@RequestParam String identifier) {
        return unitService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public UnitDto updatePrice(@RequestBody UnitDto unitDto) {
       return unitService.update(unitDto);
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public UnitDto toggle(@RequestBody UnitDto unitDto) {
        return unitService.changeToggleStatus(unitDto.getIdentifier(), unitDto.isStatus());
    }

    @PostMapping("/findActiveStatus")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public List<UnitDto> findActive() {
        return unitService.findActiveStatus();
    }
}
