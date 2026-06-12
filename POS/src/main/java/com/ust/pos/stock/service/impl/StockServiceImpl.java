package com.ust.pos.stock.service.impl;

import com.ust.pos.dto.StockDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.StockService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Transactional
@Service
public class StockServiceImpl implements StockService {

    public static final String STOCK_WITH_IDENTIFIER = "Stock with identifier - ";
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public StockDto save(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);
        if (existingStock != null) {
            stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " already exists");
            stockDto.setSuccess(false);
            return stockDto;
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public List<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findAll(pageable);
        return modelMapper.map(stockPage.getContent(), listType);
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifier(identifier), StockDto.class);
    }

    @Override
    public StockDto update(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(stockDto.getIdentifier());
        if (existingStock == null) {
            stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }
        modelMapper.map(stockDto, existingStock);
        stockRepository.save(existingStock);
        stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " Updated");
        stockDto.setSuccess(true);
        return stockDto;
    }

    @Override
    public StockDto toggleStatus(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        stock.setStatus(!stock.isStatus());
        stockRepository.save(stock);
        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public boolean delete(String identifier) {
        stockRepository.deleteByIdentifier(identifier);
        return true;
    }
}
