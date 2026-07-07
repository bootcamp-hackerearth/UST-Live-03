package com.ust.pos.api.cartentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.CartEntry;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cartentry")
@RequiredArgsConstructor
public class ApiCartEntryController extends BaseController {

    private final CartEntryService cartEntryService;

    @PostMapping("/add")
    public CartEntryDto addPost(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.save(cartEntryDto);
    }

    @PostMapping("/list")
    public WsDto<CartEntryDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<CartEntry> example = buildGlobalSearchSpec(CartEntry.class, paginationDto.getKeyword());
            if (example != null) {
                return cartEntryService.findAll(example, pageable);
            }
        }
        return cartEntryService.findAll(pageable);
    }

    @GetMapping("/get")
    public CartEntryDto get(@RequestParam("identifier") String identifier) {
        return cartEntryService.findByIdentifier(identifier);
    }

}