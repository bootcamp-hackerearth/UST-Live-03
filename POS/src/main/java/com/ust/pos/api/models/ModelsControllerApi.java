package com.ust.pos.api.models;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Models;
import com.ust.pos.models.service.ModelsService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
public class ModelsControllerApi extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/models/list";

    private final ModelsService modelsService;

    public ModelsControllerApi(ModelsService modelsService) {
        this.modelsService = modelsService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
    public WsDto<ModelsDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortfield());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Models> example = buildGlobalSearchSpec(Models.class, paginationDto.getKeyword());
            if (example != null) {
                return modelsService.findAll(example, pageable);
            }
        }

        return modelsService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
    public ModelsDto addPost(@RequestBody ModelsDto modelsDto) {
        return modelsService.save(modelsDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
    public ModelsDto updatePage(@RequestParam String identifier) {
        return modelsService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
    public ModelsDto updatePost(@RequestBody ModelsDto modelsDto) {
        return modelsService.update(modelsDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
    public ModelsDto delete(@RequestBody ModelsDto modelsDto) {
        ModelsDto response = new ModelsDto();
        try {
            modelsService.delete(modelsDto.getIdentifier());
            response.setSuccess(true);
            response.setMessage("Models deleted successfully");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Delete failed");
        }
        return response;
    }

    @PatchMapping("/toggle")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
    public ModelsDto toggleStatus(@RequestBody ModelsDto modelsDto) {
        return modelsService.toggleStatus(modelsDto.getIdentifier());
    }

    @GetMapping("/active")
    public List<ModelsDto> getActiveModels() {
        return modelsService.findActiveModels();
    }
}