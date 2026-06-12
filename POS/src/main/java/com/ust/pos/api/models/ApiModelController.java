package com.ust.pos.api.models;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/model")
public class ApiModelController extends BaseController {

    @Autowired
    ModelService modelService;

    @PostMapping("/list")
    public WsDto<ModelDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable= getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return modelService.findAll(pageable);
    }

    @PostMapping("/add")
    public ModelDto addModel(@RequestBody ModelDto modelDto) {
        return modelService.save(modelDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            modelService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/get")
    public ModelDto update(@RequestParam String identifier) {
        return modelService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ModelDto updatePrice(@RequestBody ModelDto modelDto) {
        return modelService.update(modelDto);
    }

    @PostMapping("/toggle")
    public ModelDto toggle(@RequestBody ModelDto modelDto) {
        return modelService.changeToggleStatus(modelDto.getIdentifier(), modelDto.isStatus());
    }
    @PostMapping("/findActiveStatus")
    public List<ModelDto> findActive() {
        return modelService.findActiveStatus();
    }

}
