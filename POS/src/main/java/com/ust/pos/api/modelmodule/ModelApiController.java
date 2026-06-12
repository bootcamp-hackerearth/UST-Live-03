package com.ust.pos.api.modelmodule;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modelmodule.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/model")
public class ModelApiController extends BaseController {

    @Autowired
    ModelService modelService;

    @PostMapping("/add")
    public ModelDto addPost(@RequestBody ModelDto modelDto) {
        return modelService.save(modelDto);
    }

    @PostMapping("/list")
    public WsDto<ModelDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return modelService.findAll(pageable);
    }

    @GetMapping("/get")
    public ModelDto update(@RequestParam String identifier) {
        return modelService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ModelDto updatePost(@RequestBody ModelDto modelDto) {
        return modelService.update(modelDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            modelService.deleteByIdentifier(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/togglestatus")
    public ModelDto toggle(@RequestBody ModelDto modelDto) {
        return modelService.toggleStatus(modelDto.getIdentifier(), modelDto.isStatus());
    }
}