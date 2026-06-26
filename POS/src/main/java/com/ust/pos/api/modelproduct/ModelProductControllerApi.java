package com.ust.pos.api.modelproduct;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modelproduct.service.ModelProductService;
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
@RequestMapping("/api/model")
public class ModelProductControllerApi extends BaseController {

    private final ModelProductService modelProductService;

    public ModelProductControllerApi(ModelProductService modelProductService) {
        this.modelProductService = modelProductService;
    }

    @GetMapping("/all")
    public List<ModelProductDto> all() {
        return modelProductService.findAll();
    }

    @PostMapping("/list")
    public WsDto<ModelProductDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(), paginationDto.getSortField());
        Page<ModelProductDto> pageResult = modelProductService.findAll(pageable, paginationDto.getSearch());
        WsDto<ModelProductDto> response = new WsDto<>();
        response.setContent(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPages(pageResult.getTotalPages());
        return response;
    }

    @PostMapping("/add")
    public ModelProductDto addPost(@RequestBody ModelProductDto modelProductDto) {
        return modelProductService.save(modelProductDto);
    }

    @GetMapping("/get")
    public ModelProductDto update(@RequestParam String identifier) {
        return modelProductService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ModelProductDto updatePost(@RequestBody ModelProductDto modelProductDto) {
        return modelProductService.update(modelProductDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            modelProductService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    public boolean toggleStatus(@RequestParam String identifier) {
        try {
            modelProductService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}