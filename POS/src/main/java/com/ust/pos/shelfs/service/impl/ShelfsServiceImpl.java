package com.ust.pos.shelfs.service.impl;
import com.ust.pos.CommonService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Shelfs;
import com.ust.pos.model.ShelfsRepository;
import com.ust.pos.shelfs.service.ShelfsService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ShelfsServiceImpl extends CommonService implements ShelfsService {

    private final ShelfsRepository shelfRepository;
    private final ModelMapper modelMapper;

    public ShelfsServiceImpl(ShelfsRepository shelfRepository, ModelMapper modelMapper) {
        this.shelfRepository = shelfRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ShelfsDto save(ShelfsDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelfs existingShelf = shelfRepository.findByIdentifier(identifier);
        if (existingShelf != null) {
            if(existingShelf.isDeleted()){
                shelfDto.setMessage("Shelf identifier - " + identifier + " not available");
                shelfDto.setSuccess(false);
                return shelfDto;
            }
            shelfDto.setMessage("Shelf with identifier - " + identifier + " already exists");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        Shelfs shelf = modelMapper.map(shelfDto, Shelfs.class);
        setAuditFields(shelf,true);
        shelfRepository.save(shelf);
        return shelfDto;
    }

    @Override
    public ShelfsDto update(ShelfsDto shelfDto) {
        Shelfs existingShelf = shelfRepository.findByIdentifier(shelfDto.getIdentifier());
        if (existingShelf == null) {
            shelfDto.setSuccess(false);
            shelfDto.setMessage("Shelf not found");
            return shelfDto;
        }
        modelMapper.map(shelfDto, existingShelf);
        setAuditFields(existingShelf,false);
        shelfRepository.save(existingShelf);
        return shelfDto;
    }

    @Override
    public void delete(String identifier) {
        Shelfs shelfs=shelfRepository.findByIdentifier(identifier.trim());
        softDelete(shelfs);
        setAuditFields(shelfs,false);
    }

    @Override
    public WsDto<ShelfsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfsDto>>() {}.getType();
        Page<Shelfs> shelfsPage = shelfRepository.findByIsDeletedFalse(pageable);
        WsDto<ShelfsDto> shelfsDtoWsDto = new WsDto<>();
        shelfsDtoWsDto.setDtoList(modelMapper.map(shelfsPage.getContent(), listType));
        shelfsDtoWsDto.setTotalRecords(shelfsPage.getTotalElements());
        shelfsDtoWsDto.setTotalPages(shelfsPage.getTotalPages());
        shelfsDtoWsDto.setSizePerPage(pageable.getPageSize());
        shelfsDtoWsDto.setPage(pageable.getPageNumber());
        return shelfsDtoWsDto;
    }

    @Override
    public ShelfsDto findByIdentifier(String identifier) {
        Shelfs shelfs=shelfRepository.findByIdentifierAndIsDeletedFalse(identifier);
        if(shelfs==null){
            throw new ResourceNotFoundException("shelf with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(shelfs, ShelfsDto.class);
    }

    @Override
    public List<ShelfsDto> findAllActive() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(shelfRepository.findByStatusTrueAndIsDeletedFalse(), listType);
    }

    @Override
    public void toggleStatus(String identifier) {
        Shelfs shelf = shelfRepository.findByIdentifier(identifier);
        shelf.setStatus(!shelf.getStatus());
        setAuditFields(shelf,false);
        shelfRepository.save(shelf);
    }

    @Override
    public WsDto<ShelfsDto> findAll(Specification<Shelfs> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        Page<Shelfs> page = shelfRepository.findAll(example, pageable);
        WsDto<ShelfsDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}
