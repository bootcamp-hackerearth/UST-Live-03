package com.ust.pos.api.unit;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.unit.service.UnitService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/unit")
@PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
public class UnitApiController extends BaseController {

    private final UnitService unitService;

    public UnitApiController(UnitService unitService) {
        this.unitService = unitService;
    }

    @PostMapping("/list")
    public WsDto<UnitDto> home(@RequestBody PaginationDto paginationDto) throws Exception
    {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        Example<Unit> example = buildSearchProbe(Unit.class, paginationDto.getSearch());

        Page<UnitDto> pageResult = unitService.findAll(example, pageable);

        WsDto<UnitDto> response = new WsDto<>();

        response.setDtoList(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPage(pageResult.getTotalPages());
        response.setTotalRecords(pageResult.getTotalElements());

        return response;
    }
    @GetMapping("/toggleStatus")
    public Boolean toggleStatus(@RequestParam String identifier)
    {
        try
        {
            unitService.toggleStatus(identifier);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    @PostMapping("/add")
    public UnitDto addModel(@RequestBody UnitDto unitDto)
    {
        return unitService.save(unitDto);
    }

    @GetMapping("/get")
    public UnitDto update(@RequestParam String identifier)
    {
        return unitService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public UnitDto updatePost(@RequestBody UnitDto unitDto)
    {
        return unitService.update(unitDto);
    }

    @DeleteMapping("/delete")
    public Boolean delete(@RequestParam String identifier)
    {
        try {
            unitService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}