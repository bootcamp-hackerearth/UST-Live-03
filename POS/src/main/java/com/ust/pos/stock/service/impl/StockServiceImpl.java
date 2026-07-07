package com.ust.pos.stock.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.StockService;
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
public class StockServiceImpl extends BaseService implements StockService {
    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public StockServiceImpl(StockRepository stockRepository,
                            ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public StockDto save(StockDto stockDto) {
        stockDto.setIdentifier(
                stockDto.getProduct() + "_" + stockDto.getWarehouse()
        );
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);
        if (existingStock != null) {
            if (Boolean.TRUE.equals(existingStock.getDeleted())) {
                stockDto.setMessage("Stock - " + identifier + " was deleted and cannot be recreated");
            } else {
                stockDto.setMessage("stock with identifier - " + identifier + " already exists");
            }
            stockDto.setSuccess(false);
            return stockDto;
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        setCreatedDetails(stock);
        stockRepository.save(stock);
        stockDto.setSuccess(true);
        return stockDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingStock == null) {
            stockDto.setMessage("Stock with identifier - " + identifier + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }
        modelMapper.map(stockDto, existingStock);
        setModifiedDetails(existingStock);
        stockRepository.save(existingStock);
        stockDto.setSuccess(true);
        return stockDto;
    }

    @Override
    public void delete(String identifier) {
        Stock stock = stockRepository.findByIdentifierAndDeletedFalse(identifier);
        if (stock != null) {
            softDelete(stock);
            setModifiedDetails(stock);
            stockRepository.save(stock);
        }
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findByDeletedFalse(pageable);
        WsDto<StockDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(stockPage.getContent(), listType));
        wsDto.setTotalRecords(stockPage.getTotalElements());
        wsDto.setTotalPages(stockPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        Stock stock = stockRepository.findByIdentifierAndDeletedFalse(identifier);
        if (stock == null) {
            throw new ResourceNotFoundException("stock with identifier " + identifier + " not found");
        }
        return modelMapper.map(stock, StockDto.class);
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
}