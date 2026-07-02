package com.ust.pos.stock.service.impl;

import com.ust.pos.api.BaseService;
import com.ust.pos.dto.StockDto;
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
    private final ModelMapper modelMapper;
    private final StockRepository stockRepository;

    public StockServiceImpl(StockRepository stockRepository, ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public StockDto save(StockDto stockDto) {
        if (stockDto.getQuantity() > 0) {
            stockDto.setStockStatus("IN_STOCK");
        } else {
            stockDto.setStockStatus("OUT_OF_STOCK");
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        if (stockRepository.findByIdentifierAndDeletedFalse(stock.getIdentifier()) != null) {
            stockDto.setMessage("The Product " + stockDto.getIdentifier() + "Already Exists");
            stockDto.setSuccess(false);
            return stockDto;
        }
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        Stock existingStock = stockRepository.findByIdentifierAndDeletedFalse(stockDto.getIdentifier());
        if (existingStock == null) {
            stockDto.setMessage("Stock with " + stockDto.getIdentifier() + "not found.");
            stockDto.setSuccess(false);
            return stockDto;
        }
        if (stockDto.getQuantity() > 0) {
            stockDto.setStockStatus("IN_STOCK");
        } else {
            stockDto.setStockStatus("OUT_OF_STOCK");
        }
        modelMapper.map(stockDto, existingStock);
        stockRepository.save(existingStock);
        return stockDto;
    }

    @Override
    public List<StockDto> findAll() {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        return modelMapper.map(stockRepository.findByDeletedFalse(), listType);
    }

    @Override
    public void delete(String identifier) {
        Stock stock = stockRepository.findByIdentifierAndDeletedFalse(identifier);
        if (stock != null) {
            stock.setDeleted(true);
            stockRepository.save(stock);
        }
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        Stock stock = stockRepository.findByIdentifierAndDeletedFalse(identifier);
        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public void updateStatusOnly(String identifier, boolean status) {
        Stock stock = stockRepository.findByIdentifierAndDeletedFalse(identifier);
        stock.setStatus(status);
        stockRepository.save(stock);
    }

    @Override
    public Page<StockDto> findAll(String search, Pageable pageable) {
        Page<Stock> stocks;

        if (search != null && !search.trim().isEmpty()) {
            Specification<Stock> specification =
                    buildGlobalSearchSpec(Stock.class, search);
            stocks = stockRepository.findAll(specification, pageable);
        } else {
            stocks = stockRepository.findByDeletedFalse(pageable);
        }

        return stocks.map(stock ->
                modelMapper.map(stock, StockDto.class));
    }
}