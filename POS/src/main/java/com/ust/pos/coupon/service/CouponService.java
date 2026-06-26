package com.ust.pos.coupon.service;

import com.ust.pos.dto.CouponDto;

import java.util.List;

public interface CouponService {

    CouponDto save(CouponDto couponDto);

    CouponDto update(Long id, CouponDto couponDto);

    CouponDto findByCode(String code);

    List<CouponDto> findAll();

    void delete(Long id);

    CouponDto deactivate(Long id);
}