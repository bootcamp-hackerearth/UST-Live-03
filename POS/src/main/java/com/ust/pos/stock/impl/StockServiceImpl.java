package com.ust.pos.stock.impl;

import com.ust.pos.CommonService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.StockService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.BooleanUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class StockServiceImpl extends CommonService implements StockService {

    private static final String STOCK_WITH_IDENTIFIER = "Stock with identifier - ";

    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public StockServiceImpl(StockRepository stockRepository,
                            ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public StockDto save(StockDto dto) {

        if (dto == null || dto.getProductIdentifier() == null || dto.getWarehouseIdentifier() == null) {
            throw new IllegalArgumentException("Product and Warehouse are required");
        }
        String identifier = "STK_" + dto.getProductIdentifier() + "_" + dto.getWarehouseIdentifier();
        dto.setIdentifier(identifier);

        Stock existing = stockRepository.findByIdentifier(identifier);

        if (existing != null) {
            if (BooleanUtils.isNotTrue(existing.getDeleted())) {
                dto.setSuccess(false);
                dto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " already exists");
                return dto;
            }

            dto.setSuccess(false);
            dto.setMessage(STOCK_WITH_IDENTIFIER + identifier +
                    " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        Stock stock = modelMapper.map(dto, Stock.class);
        setAuditFields(stock, true);

        stockRepository.save(stock);

        dto.setSuccess(true);
        dto.setMessage("Stock created successfully");

        return dto;
    }

    @Override
    public StockDto update(StockDto dto) {

        String identifier = dto.getIdentifier();
        Stock existing = stockRepository.findByIdentifier(identifier);

        if (existing == null) {
            dto.setSuccess(false);
            dto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        if (BooleanUtils.isTrue(existing.getDeleted())) {
            dto.setSuccess(false);
            dto.setMessage(STOCK_WITH_IDENTIFIER + identifier +
                    " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        modelMapper.map(dto, existing);
        setAuditFields(existing, false);

        stockRepository.save(existing);

        dto.setSuccess(true);
        dto.setMessage("Stock updated successfully");

        return dto;
    }

    @Override
    @Transactional
    public void deleteByIdentifier(String identifier) {

        Stock stock = stockRepository.findByIdentifier(identifier);

        if (stock != null) {
            softDelete(stock);
            setAuditFields(stock, false);
            stockRepository.save(stock);
        }
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        Stock stock =stockRepository.findByIdentifier(identifier);
        if(stock==null){
            throw new ResourceNotFoundException("Data Cannot Found");
        }
        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();


        Page<Stock> page = stockRepository.findByDeletedFalse(pageable);

        WsDto<StockDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
    @Override
    public WsDto<StockDto> findAll(Specification<Stock> example, Pageable pageable) {

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> page = stockRepository.findAll(example, pageable);

        WsDto<StockDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public StockDto toggleStatus(String identifier) {

        Stock stock = stockRepository.findByIdentifier(identifier);

        if (stock == null) {
            StockDto dto = new StockDto();
            dto.setSuccess(false);
            dto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        stock.setStatus(!stock.getStatus());
        setAuditFields(stock, false);

        stockRepository.save(stock);

        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public List<StockDto> findIfTrue() {

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();

        return modelMapper.map(
                stockRepository.findByStatusIsTrueAndDeletedFalse(),
                listType
        );
    }
}
