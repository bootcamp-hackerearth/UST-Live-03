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
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage()
                , paginationDto.getSortDirection(), paginationDto.getSortField());
        return modelsService.findAll(pageable);
    }

    @GetMapping("/getAllActive")
    public List<ModelsDto> getAllActive() {
        return modelsService.findAllActive();
    }

    @PostMapping("/add")
    public ModelsDto add(@RequestBody ModelsDto modelsDto) {
        return modelsService.save(modelsDto);
    }

    @GetMapping("/get")
    public ModelsDto getByIdentifier(@RequestParam String identifier) {
        return modelsService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ModelsDto update(@ModelAttribute ModelsDto modelsDto) {
        return modelsService.update(modelsDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            modelsService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle")
    public ModelsDto toggleStatus(@RequestParam String identifier) {
        return modelsService.toggleStatus(identifier);
    }
}
