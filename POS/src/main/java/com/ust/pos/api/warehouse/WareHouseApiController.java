package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.warehouse.service.WareHouseService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wareHouse")
public class WareHouseApiController extends BaseController {

    private final WareHouseService wareHouseService;

    public WareHouseApiController(WareHouseService wareHouseService) {
        this.wareHouseService = wareHouseService;
    }

    @PostMapping("/list")
    public WsDto<WareHouseDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return wareHouseService.findAll(pageable);
    }

    @PostMapping("/add")
    public WareHouseDto addPost(@RequestBody WareHouseDto wareHouseDto) {
        return wareHouseService.save(wareHouseDto);
    }

    @GetMapping("/get")
    public WareHouseDto update(@RequestParam String identifier) {
        return wareHouseService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public WareHouseDto updatePost(@RequestBody WareHouseDto wareHouseDto) {
        return wareHouseService.update(wareHouseDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            wareHouseService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    public WareHouseDto toggle(@RequestParam String identifier) {
        return wareHouseService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<WareHouseDto> findByStatus() {
        return wareHouseService.findIfTrue();
    }
}