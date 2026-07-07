package com.ust.pos.api.rack;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Rack;
import com.ust.pos.rack.service.RackService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rack")
public class RackControllerApi extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/rack/list";

    private final RackService rackService;

    public RackControllerApi(RackService rackService) {
        this.rackService = rackService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public WsDto<RackDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortfield());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Rack> example = buildGlobalSearchSpec(Rack.class, paginationDto.getKeyword());
            if (example != null) {
                return rackService.findAll(example, pageable);
            }
        }

        return rackService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public RackDto addPost(@RequestBody RackDto rackDto) {
        return rackService.save(rackDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public RackDto updatePage(@RequestParam String identifier) {
        return rackService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public RackDto updatePost(@RequestBody RackDto rackDto) {
        return rackService.update(rackDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public RackDto delete(@RequestBody RackDto rackDto) {
        RackDto response = new RackDto();
        try {
            rackService.delete(rackDto.getIdentifier());
            response.setSuccess(true);
            response.setMessage("Rack deleted successfully");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Delete failed");
        }
        return response;
    }

    @GetMapping("/activeracks")
    public List<RackDto> getActiveRacks() {
        return rackService.getActiveRacks();
    }

    @PatchMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public RackDto toggleStatus(@RequestBody RackDto rackDto) {
        return rackService.toggleStatus(rackDto.getIdentifier());
    }
}