package com.ust.pos.shelfs.service;

import com.ust.pos.dto.ShelfsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ShelfsService {

    ShelfsDto save(ShelfsDto shelfsDto);

    ShelfsDto update(ShelfsDto shelfsDto);

    boolean delete(String identifier);

    List<ShelfsDto> findAll(Pageable pageable);

    ShelfsDto findByIdentifier(String identifier);

    ShelfsDto toggleStatus(String identifier);

    List<ShelfsDto> findIfTrue();

}
