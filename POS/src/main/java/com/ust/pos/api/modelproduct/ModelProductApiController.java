package com.ust.pos.api.modelproduct;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.ModelProduct;
import com.ust.pos.modelproduct.service.ModelProductService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/model")
@PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
public class ModelProductApiController extends BaseController {

    private final ModelProductService modelProductService;


    public ModelProductApiController(ModelProductService modelProductService) {
        this.modelProductService = modelProductService;
    }

    @PostMapping("/list")
    public WsDto<ModelProductDto> home(@RequestBody PaginationDto paginationDto) throws Exception
    {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        Example<ModelProduct> example = buildSearchProbe(ModelProduct.class, paginationDto.getSearch());
        Page<ModelProductDto> pageResult = modelProductService.findAll(example, pageable);
        WsDto<ModelProductDto> response = new WsDto<>();

        response.setDtoList(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPage(pageResult.getTotalPages());
        response.setTotalRecords(pageResult.getTotalElements());

        return response;
    }
    @GetMapping("/toggleStatus")
    public Boolean toggleStatus(@RequestParam String identifier)
    {
        try
        {
            modelProductService.toggleStatus(identifier);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }


    @PostMapping("/add")
    public ModelProductDto addModel(@RequestBody ModelProductDto modelProductDto)
    {
        return modelProductService.save(modelProductDto);
    }

    @GetMapping("/get")
    public ModelProductDto update(@RequestParam String identifier)
    {
        return modelProductService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ModelProductDto updatePost(@RequestBody ModelProductDto modelProductDto)
    {
        return modelProductService.update(modelProductDto);
    }

    @DeleteMapping("/delete")
    public Boolean delete(@RequestParam String identifier)
    {
        try
        {
            modelProductService.delete(identifier);
        }
        catch(Exception e)
        {
            return false;
        }
        return true;
    }
}
