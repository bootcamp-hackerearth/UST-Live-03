package com.ust.pos.api.models;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.models.service.ModelsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
public class ModelsApiController extends BaseController {

    @Autowired
    private ModelsService modelsService;

    @PostMapping("/list")
    public List<ModelsDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return modelsService.findAll(pageable);
    }

    @PostMapping("/add")
    public ModelsDto add(@RequestBody ModelsDto modelsDto) {
        return modelsService.save(modelsDto);
    }

    @GetMapping("/get")
    public ModelsDto get(@RequestParam String identifier) {
        return modelsService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ModelsDto update(@RequestBody ModelsDto modelsDto) {
        return modelsService.update(modelsDto);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody ModelsDto modelsDto) {
        try {
            modelsService.delete(modelsDto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/toggleStatus")
    public ModelsDto toggleStatus(@RequestBody ModelsDto modelsDto) {
        return modelsService.toggleStatus(modelsDto.getIdentifier(), modelsDto.isStatus());
    }

    @GetMapping("/models")
    public List<ModelsDto> getActiveModel() {
        return modelsService.findActiveModel();
    }
}