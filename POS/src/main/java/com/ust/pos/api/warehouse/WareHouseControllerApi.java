package com.ust.pos.api.warehouse;
import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.warehouse.service.WareHouseService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/warehouse")
public class WareHouseControllerApi extends BaseController {
    public static final String REDIRECT_WAREHOUSE_LIST = "redirect:/warehouse/list";

    private final WareHouseService wareHouseService;

    public WareHouseControllerApi(WareHouseService wareHouseService){
        this.wareHouseService=wareHouseService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin')")
    public PageDto<WareHouseDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Warehouse> spec = buildGlobalSearchSpec(Warehouse.class, paginationDto.getKeyword());
            return wareHouseService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return wareHouseService.findAll(pageable);
    }

    @GetMapping("/identifier")
    public WareHouseDto getWarehouseByIdentifier(@RequestParam String identifier) {
        return wareHouseService.findByIdentifier(identifier);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin')")
    public WareHouseDto addPost(@RequestBody WareHouseDto wareHouseDto) {
        return wareHouseService.save(wareHouseDto);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Admin')")
    public WareHouseDto updatePost(@RequestBody WareHouseDto wareHouseDto) {
        return wareHouseService.update(wareHouseDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Admin')")
    public boolean delete(@RequestParam String identifier) {
        try {
            wareHouseService.delete(identifier);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    @PreAuthorize("hasAuthority('Admin')")
    public void toggleStatus(@RequestParam String identifier) {
        wareHouseService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<WareHouseDto> findByStatus() {
        return wareHouseService.findActiveWarehouses();
    }
}
