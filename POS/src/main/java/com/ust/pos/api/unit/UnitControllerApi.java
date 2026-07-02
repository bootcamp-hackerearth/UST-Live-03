package com.ust.pos.api.unit;
import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import com.ust.pos.unit.service.UnitService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/unit")
public class UnitControllerApi extends BaseController {
    public static final String REDIRECT_UNIT_LIST= "redirect:/unit/list";

    private final UnitService unitService;

    public UnitControllerApi(UnitService unitService){
        this.unitService=unitService;
    }

    @PostMapping("/list")
    public PageDto<UnitDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Unit> spec = buildGlobalSearchSpec(Unit.class, paginationDto.getKeyword());
            return unitService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return unitService.findAll(pageable);
    }

    @GetMapping("/identifier")
    public UnitDto getModelByIdentifier(@RequestParam String identifier) {
        return unitService.findByIdentifier(identifier);
    }

    @PostMapping("/add")
    public UnitDto addPost(@RequestBody UnitDto unitDto) {
        return unitService.save(unitDto);
    }

    @PutMapping("/update")
    public UnitDto updatePost(@RequestBody UnitDto unitDto) {
        return unitService.update(unitDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            unitService.delete(identifier);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    public void toggleStatus(@RequestParam String identifier) {
        unitService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<UnitDto> findByStatus() {
        return unitService.findActiveUnits();
    }
}
