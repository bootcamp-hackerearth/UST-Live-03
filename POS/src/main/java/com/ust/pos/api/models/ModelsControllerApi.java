package com.ust.pos.api.models;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.service.ModelsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
public class ModelsControllerApi extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/models/list";

    @Autowired
    private ModelsService modelsService;

    @PostMapping("/list")
    public WsDto<ModelsDto> list(@RequestBody PaginationDto pagination) {
        Pageable pageable = getPageable(pagination.getPage(), pagination.getSizePerPage(),
                pagination.getSortDirection(), pagination.getSortfield());
        return modelsService.findAll(pageable);
    }

    @PostMapping("/add")
    public ModelsDto addPost(@RequestBody ModelsDto modelsDto) {
        return modelsService.save(modelsDto);
    }

    @GetMapping("/get")
    public ModelsDto updatePage(@RequestParam String identifier) {
        return modelsService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ModelsDto updatePost(@RequestBody ModelsDto modelsDto) {
        return modelsService.update(modelsDto);
    }

    @PostMapping("/delete")
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

    @PostMapping("/toggle-status")
    public ModelsDto toggleStatus(@RequestBody ModelsDto modelsDto) {
        return modelsService.toggleStatus(modelsDto.getIdentifier());
    }

    @GetMapping("/active")
    public List<ModelsDto> getActiveModels() {
        return modelsService.findActiveModels();
    }
}