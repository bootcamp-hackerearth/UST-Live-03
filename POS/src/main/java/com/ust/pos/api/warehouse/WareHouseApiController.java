package com.ust.pos.api.warehouse;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.WareHouse;
import com.ust.pos.warehouse.service.WareHouseService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
public class WareHouseApiController extends BaseController {
    private final WareHouseService wareHouseService;

    public WareHouseApiController(WareHouseService wareHouseService) {
        this.wareHouseService = wareHouseService;
    }

    @PostMapping("/list")
    public WsDto<WareHouseDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<WareHouse> example = buildGlobalSearchSpec(WareHouse.class, paginationDto.getKeyword());
            if (example != null) {
                return wareHouseService.findAll(example, pageable);
            }
        }
        return wareHouseService.findAll(pageable);
    }

    @PostMapping("/add")
    public WareHouseDto add(@RequestBody WareHouseDto wareHouseDto) {
        return wareHouseService.save(wareHouseDto);
    }

    @GetMapping("/get")
    public WareHouseDto get(@RequestParam String identifier) {
        return wareHouseService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public WareHouseDto update(@RequestBody WareHouseDto wareHouseDto) {
        return wareHouseService.update(wareHouseDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody WareHouseDto wareHouseDto) {
        try {
            wareHouseService.delete(wareHouseDto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/active")
    public List<WareHouseDto> getActiveWareHouse() {
        return wareHouseService.findActiveWareHouse();
    }

    @PostMapping("/toggleStatus")
    public WareHouseDto toggleStatus(@RequestBody WareHouseDto wareHouseDto) {
        return wareHouseService.toggleStatus(wareHouseDto.getIdentifier(), wareHouseDto.isStatus());
    }
}