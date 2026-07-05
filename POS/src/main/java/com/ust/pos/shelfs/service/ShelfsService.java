package com.ust.pos.shelfs.service;

import com.ust.pos.dto.WsDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.model.Shelfs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShelfsService {
    ShelfsDto save(ShelfsDto shelfsDto);

    ShelfsDto update(ShelfsDto shelfsDto);

    void delete(String identifier);

    WsDto<ShelfsDto> findAll(Pageable pageable);

    ShelfsDto findByIdentifier(String identifier);

    List<ShelfsDto> findAllActive();

    void toggleStatus(String identifier);

    WsDto<ShelfsDto> findAll(Specification<Shelfs> example, Pageable pageable);

}
