package com.ust.pos.stock.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.StockService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class StockServiceImpl extends BaseService implements StockService {
    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public StockServiceImpl(StockRepository stockRepository, ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifier(identifier), StockDto.class);
    }

    @Override
    public StockDto save(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existing = stockRepository.findByIdentifier(identifier);
        if (existing != null) {
            if (existing.isDeleted()) {
                stockDto.setMessage("Stock with identifier" + identifier + "has been soft deleted.(Rollback by changing status)");
                stockDto.setSuccess(false);
                return stockDto;
            }
            stockDto.setMessage("Stock with identifier - " + identifier + " already exists");
            stockDto.setSuccess(false);
            return stockDto;
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        setCreatedDetails(stock);
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existing = stockRepository.findByIdentifier(identifier);
        if (existing == null) {
            stockDto.setMessage("Stock not found - " + identifier);
            stockDto.setSuccess(false);
            return stockDto;
        }
        modelMapper.map(stockDto, existing);
        setModifiedDetails(existing);
        stockRepository.save(existing);
        return stockDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        softDelete(stock);
        setModifiedDetails(stock);
        stockRepository.save(stock);
    }

    public WsDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findByDeletedFalse(pageable);

        WsDto<StockDto> stockWsDto = new WsDto<>();
        stockWsDto.setDtoList(modelMapper.map(stockPage.getContent(), listType));
        stockWsDto.setTotalRecords(stockPage.getTotalElements());
        stockWsDto.setTotalPages(stockPage.getTotalPages());
        stockWsDto.setSizePerPage(pageable.getPageSize());
        stockWsDto.setPage(pageable.getPageNumber());

        return stockWsDto;
    }

    @Override
    @Transactional
    public StockDto toggleStatus(String identifier, boolean status) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        if (stock != null) {
            stock.setStatus(status);
            setModifiedDetails(stock);
            stockRepository.save(stock);
        }
        return modelMapper.map(stock, StockDto.class);
    }
}