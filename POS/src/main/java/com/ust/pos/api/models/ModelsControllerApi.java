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

    public static final String REDIRECT_NODE_LIST = "redirect:/models/list";
    public static final String ROLES_LIST = "modelsList";


    @Autowired
    private ModelsService modelsService;

    @PostMapping("/list")
    public WsDto<ModelsDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return modelsService.findAll(pageable);

    }


    @PostMapping("/add")
    public ModelsDto addPost(@RequestBody ModelsDto modelsDto) {
        return modelsService.save(modelsDto);
    }

    @GetMapping("/update")
    public ModelsDto update(@RequestParam Long id) {

        return modelsService.findById(id);

    }

    @PostMapping("/update")
    public ModelsDto updatePost(@RequestBody ModelsDto modelsDto) {
        return modelsService.update(modelsDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam Long id) {
        try {
            modelsService.deleteById(id);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/changeStatus")
    public ModelsDto toggle(@RequestBody ModelsDto modelsDto) {

        return modelsService.changeModelsStatus(modelsDto.getIdentifier(), modelsDto.isStatus());

    }

    @GetMapping("/findAllActive")
    public List<ModelsDto> allactive() {
        return modelsService.findActiveModels();
    }
}