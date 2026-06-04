package com.ust.pos.shelfs.service;

import com.ust.pos.dto.ShelfsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShelfsService {
    ShelfsDto save(ShelfsDto shelfsDto);

    ShelfsDto update(ShelfsDto shelfsDto);

    void delete(String identifier);

    List<ShelfsDto> findAll(Pageable pageable);

    ShelfsDto findByIdentifier(String identifier);

    List<ShelfsDto> findAllActive();

    void toggleStatus(String identifier);

}
