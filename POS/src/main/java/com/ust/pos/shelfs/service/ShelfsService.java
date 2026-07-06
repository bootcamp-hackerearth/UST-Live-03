package com.ust.pos.shelfs.service;

import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelfs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ShelfsService {

    ShelfsDto save(ShelfsDto shelfsDto);
    ShelfsDto update(ShelfsDto shelfsDto);
    boolean delete(String identifier);
    WsDto<ShelfsDto> findAll(Pageable pageable);
    ShelfsDto findByIdentifier(String identifier);
    ShelfsDto toggleStatus(String identifier);
    List<ShelfsDto> findIfTrue();
    WsDto<ShelfsDto> findAll(Specification<Shelfs> example, Pageable pageable, String keyword);

}