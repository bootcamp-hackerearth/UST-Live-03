package com.ust.pos.racks.service;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Racks;
import com.ust.pos.model.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface RacksService {
   RacksDto save(RacksDto racksDto);

   RacksDto update(RacksDto racksDto);

   boolean delete(String identifier);

   PageDto<RacksDto> findAll(Pageable pageable);

   RacksDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<RacksDto> findActiveRacks();

    PageDto<RacksDto> findAll(Specification<Racks> spec, Pageable pageable, String keyword);
}
