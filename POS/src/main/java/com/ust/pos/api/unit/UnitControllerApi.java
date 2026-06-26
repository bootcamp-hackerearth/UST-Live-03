package com.ust.pos.api.unit;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.unit.service.UnitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/unit")
public class UnitControllerApi extends BaseController {

    private final UnitService unitService;

    public UnitControllerApi(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping("/all")
    public List<UnitDto> all() {
        return unitService.findAll();
    }

    @PostMapping("/list")
    public WsDto<UnitDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(), paginationDto.getSortField());
        Page<UnitDto> pageResult = unitService.findAll(pageable, paginationDto.getSearch());
        WsDto<UnitDto> response = new WsDto<>();
        response.setContent(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPages(pageResult.getTotalPages());
        return response;
    }

    @PostMapping("/add")
    public UnitDto addPost(@RequestBody UnitDto unitDto) {
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
    public boolean toggleStatus(@RequestParam String identifier) {
        try {
            unitService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}