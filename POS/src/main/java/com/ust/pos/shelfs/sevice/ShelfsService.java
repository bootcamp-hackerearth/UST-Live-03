package com.ust.pos.shelfs.sevice;
import java.util.List;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface ShelfsService {
    ShelfsDto findByIdentifier(String identifier);

    ShelfsDto save(ShelfsDto shelfDto);

    ShelfsDto update(ShelfsDto shelfDto);

    void delete(String identifier);

    WsDto<ShelfsDto> findAll(Pageable pageable);

    List<ShelfsDto> findActiveStatus();

    ShelfsDto changeToggleStatus(String identifier, boolean status);
}
