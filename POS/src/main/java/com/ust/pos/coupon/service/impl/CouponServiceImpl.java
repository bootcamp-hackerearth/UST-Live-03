package com.ust.pos.coupon.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.coupon.service.CouponService;
import com.ust.pos.dto.CouponDto;
import com.ust.pos.model.Coupon;
import com.ust.pos.model.CouponRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponServiceImpl extends CommonService implements CouponService {

    private static final String COUPON_NOT_FOUND = "Coupon not found";
    private final CouponRepository couponRepository;
    private final ModelMapper modelMapper;

    public CouponServiceImpl(
            CouponRepository couponRepository,
            ModelMapper modelMapper) {
        this.couponRepository = couponRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CouponDto save(CouponDto dto) {
        Coupon coupon = modelMapper.map(dto, Coupon.class);

        coupon.setUsedCount(0);
        coupon.setActive(true);
        setAuditFields(coupon, true);
        Coupon saved = couponRepository.save(coupon);
        return modelMapper.map(saved, CouponDto.class);
    }

    @Override
    public CouponDto update(Long id, CouponDto dto) {
        Coupon existing = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(COUPON_NOT_FOUND));
        existing.setCode(dto.getCode());
        existing.setType(dto.getType());
        existing.setValue(dto.getValue());
        existing.setExpiryDate(dto.getExpiryDate());
        existing.setUsageLimit(dto.getUsageLimit());
        existing.setActive(dto.isActive());
        setAuditFields(existing, true);
        Coupon saved = couponRepository.save(existing);
        return modelMapper.map(saved, CouponDto.class);
    }

    @Override
    public CouponDto findByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException(COUPON_NOT_FOUND));

        return modelMapper.map(coupon, CouponDto.class);
    }

    @Override
    public List<CouponDto> findAll() {
        return couponRepository.findAll()
                .stream()
                .map(c -> modelMapper.map(c, CouponDto.class))
                .toList();
    }

    @Override
    public void delete(Long id) {
        couponRepository.deleteById(id);
    }

    @Override
    public CouponDto deactivate(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(COUPON_NOT_FOUND));

        coupon.setActive(false);
        setAuditFields(coupon, false);
        Coupon saved = couponRepository.save(coupon);
        return modelMapper.map(saved, CouponDto.class);
    }
}