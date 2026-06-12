package com.ust.pos.shelfs.service;

import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface ShelfsService {
    ShelfsDto save(ShelfsDto brandDto);

    ShelfsDto update(ShelfsDto brandDto);

    void delete(String identifier);

    WsDto<ShelfsDto> findAll(Pageable pageable);

    ShelfsDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);
}
