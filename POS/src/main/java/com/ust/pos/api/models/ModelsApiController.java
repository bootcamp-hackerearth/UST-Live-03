package com.ust.pos.api.models;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Models;
import com.ust.pos.models.service.ModelsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'STOCK_MANAGER', 'SUPPORT')")
public class ModelsApiController extends BaseController {

    private final ModelsService modelsService;

    public ModelsApiController(ModelsService modelsService) {
        this.modelsService = modelsService;
    }

    @PostMapping("/list")
    public WsDto<ModelsDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Models> example = buildGlobalSearchSpec(Models.class, paginationDto.getKeyword());
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

    @GetMapping("/get")
    public ModelsDto update(@RequestParam String identifier) {
        return modelsService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ModelsDto updatePost(@RequestBody ModelsDto modelsDto) {
        return modelsService.update(modelsDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            modelsService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    public ModelsDto toggle(@RequestParam String identifier) {
        return modelsService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<ModelsDto> findByStatus() {
        return modelsService.findIfTrue();
    }
}