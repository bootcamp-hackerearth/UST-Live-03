package com.ust.pos.api.coupon;

import com.ust.pos.coupon.service.CouponService;
import com.ust.pos.dto.CouponDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupon")
public class CouponApiController {

    private final CouponService couponService;

    public CouponApiController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/add")
    public CouponDto create(@RequestBody CouponDto dto) {
        return couponService.save(dto);
    }

    @GetMapping("/list")
    public List<CouponDto> getAll() {
        return couponService.findAll();
    }

    @GetMapping("/get")
    public CouponDto getByCode(@RequestParam String code) {
        return couponService.findByCode(code);
    }

    @PutMapping("/update/{id}")
    public CouponDto update(@PathVariable Long id,
                            @RequestBody CouponDto dto) {
        return couponService.update(id, dto);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        couponService.delete(id);
        return "Deleted successfully";
    }

    @PostMapping("/deactivate/{id}")
    public CouponDto deactivate(@PathVariable Long id) {
        return couponService.deactivate(id);
    }
}