package com.ust.pos.shelfs.service;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.ShelfsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShelfsService {
    ShelfsDto save(ShelfsDto shelfsDto);

    ShelfsDto update(ShelfsDto shelfsDto);

    boolean delete(String identifier);

    PageDto<ShelfsDto> findAll(Pageable pageable);

    ShelfsDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<ShelfsDto> findActiveShelves();
}
