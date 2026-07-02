package com.ust.pos.unit.service.impl;
import com.ust.pos.CommonService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UnitServiceImpl extends CommonService implements UnitService {

    private final ModelMapper modelMapper;
    private final UnitRepository unitRepository;

    public UnitServiceImpl(ModelMapper modelMapper, UnitRepository unitRepository) {
        this.modelMapper = modelMapper;
        this.unitRepository = unitRepository;
    }

    @Override
    public UnitDto save(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Unit existingmodel = unitRepository.findByIdentifier(identifier);
        if (existingmodel != null) {
            if(existingmodel.isDeleted()){
                unitDto.setMessage("Unit identifier - " + identifier + " not available");
                unitDto.setSuccess(false);
                return unitDto;
            }
            unitDto.setMessage("Model - " + identifier + " already exists");
            unitDto.setSuccess(false);
            return unitDto;
        }
        Unit unit = modelMapper.map(unitDto, Unit.class);
        setAuditFields(unit,true);
        unitRepository.save(unit);
        return unitDto;
    }

    @Override
    public UnitDto update(UnitDto unitDto) {
        String identifier = unitDto.getIdentifier();
        Optional<Unit> optionalUnit = unitRepository.findById(unitDto.getId());
        if (optionalUnit.isEmpty()) {
            unitDto.setSuccess(false);
            return unitDto;
        } else {
            Unit existingmodel = optionalUnit.get();
            if (!identifier.equals(existingmodel.getIdentifier()) && unitRepository.findByIdentifier(identifier) != null) {
                unitDto.setSuccess(false);
                unitDto.setMessage("Model Already Exists");
                return unitDto;
            } else {
                modelMapper.map(unitDto, existingmodel);
                setAuditFields(existingmodel,false);
                unitRepository.save(existingmodel);
                unitDto.setSuccess(true);
            }
            return unitDto;
        }
    }

    @Override
    public UnitDto findByIdentifier(String identifier) {
        return modelMapper.map(unitRepository.findByIdentifier(identifier), UnitDto.class);
    }

    @Override
    public WsDto<UnitDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<UnitDto>>() {}.getType();
        Page<Unit> unitPage = unitRepository.findByIsDeletedFalse(pageable);
        WsDto<UnitDto> unitDtoWsDto = new WsDto<>();
        unitDtoWsDto.setDtoList(modelMapper.map(unitPage.getContent(), listType));
        unitDtoWsDto.setTotalRecords(unitPage.getTotalElements());
        unitDtoWsDto.setTotalPages(unitPage.getTotalPages());
        unitDtoWsDto.setSizePerPage(pageable.getPageSize());
        unitDtoWsDto.setPage(pageable.getPageNumber());
        return unitDtoWsDto;
    }

    @Override
    public void delete(String identifier) {
        Unit unit=unitRepository.findByIdentifier(identifier.trim());
        softDelete(unit);
        setAuditFields(unit,false);
    }

    @Override
    public void toggleStatus(String identifier) {
        Unit unit = unitRepository.findByIdentifier(identifier);
        unit.setStatus(!unit.getStatus());
        setAuditFields(unit,false);
        unitRepository.save(unit);
    }

    @Override
    public List<UnitDto> findAllActive() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(unitRepository.findByStatusTrueAndIsDeletedFalse(), listType);
    }

    @Override
    public WsDto<UnitDto> findAll(Specification<Unit> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Page<Unit> page = unitRepository.findAll(example, pageable);
        WsDto<UnitDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}