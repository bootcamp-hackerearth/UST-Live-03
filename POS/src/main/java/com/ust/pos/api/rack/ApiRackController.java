package com.ust.pos.api.rack;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Rack;
import com.ust.pos.rack.service.RackService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rack")
@RequiredArgsConstructor
public class ApiRackController extends BaseController {

    public static final String REDIRECT_LIST = "redirect:/rack/list";

    private final RackService rackService;

    @PostMapping("/list")
    public WsDto<RackDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Rack> example = buildGlobalSearchSpec(Rack.class, paginationDto.getKeyword());
            if (example != null) {
                return rackService.findAll(example, pageable);
            }
        }
        return rackService.findAll(pageable);
    }

    @PostMapping("/add")
    public RackDto addPost(@RequestBody RackDto rackDto) {
        return rackService.save(rackDto);
    }

    @GetMapping("/get")
    public RackDto get(@RequestParam String identifier) {
        return rackService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public RackDto update(@RequestBody RackDto rackDto) {
        return rackService.update(rackDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            rackService.delete(identifier);
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    @PatchMapping("/toggle-status")
    public RackDto toggle(@RequestParam String identifier) {
        return rackService.toggleStatus(identifier);
    }

    @GetMapping("/findallactive")
    public List<RackDto> findAllActive() {
        return rackService.findAllActive();
    }
}