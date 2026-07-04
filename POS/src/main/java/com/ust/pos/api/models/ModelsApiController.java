package com.ust.pos.api.models;


import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Models;
import com.ust.pos.models.service.ModelService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
public class ModelsApiController extends BaseController {

    private final ModelService modelsService;

    public ModelsApiController(ModelService modelsService) {
        this.modelsService = modelsService;
    }

    @PostMapping("/list")
    public WsDto<ModelsDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getSearch())) {
            Specification<Models> example = buildGlobalSearchSpec(Models.class, paginationDto.getSearch());
            if (example != null) {
                return modelsService.findAll(example, pageable);
            }
        }
        return modelsService.findAll(pageable);
    }


    @PostMapping("/add")
    public ModelsDto addPost(@RequestBody ModelsDto modelsDto) {

        return modelsService.save(modelsDto);
    }

    @GetMapping("/{identifier}")
    public ModelsDto update(@PathVariable String identifier) {

        return modelsService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ModelsDto updatePost(@ModelAttribute ModelsDto modelsDto) {

        return modelsService.update(modelsDto);
    }

    @GetMapping("/getactive")
    public List<ModelsDto> getActiveModels() {

        return modelsService.findActiveModels();
    }

    @PatchMapping("/toggle")
    public boolean toggleStatus(@RequestBody String identifier) {

        try {
            modelsService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody ModelsDto modelsDto) {

        String identifier = modelsDto.getIdentifier();

        try {
            modelsService.delete(identifier);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}


