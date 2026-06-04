package com.ust.pos.api.rack;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.RackDto;
import com.ust.pos.rack.service.RackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rack")
public class RackControllerApi extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/rack/list";

    @Autowired
    private RackService rackService;

    @PostMapping("/list")
    public List<RackDto> list(@RequestBody PaginationDto pagination) {
        Pageable pageable = getPageable(pagination.getPage(), pagination.getSizePerPage(),
                pagination.getSortDirection(), pagination.getSortfield());
        return rackService.findAll(pageable);
    }

    @PostMapping("/add")
    public RackDto addPost(@RequestBody RackDto rackDto) {
        return rackService.save(rackDto);
    }

    @GetMapping("/get")
    public RackDto updatePage(@RequestParam String identifier) {
        return rackService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public RackDto updatePost(@RequestBody RackDto rackDto) {
        return rackService.update(rackDto);
    }

    @PostMapping("/delete")
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

    @PostMapping("/toggle")
    public RackDto toggleStatus(@RequestBody RackDto rackDto) {
        return rackService.toggleStatus(rackDto.getIdentifier());
    }
}