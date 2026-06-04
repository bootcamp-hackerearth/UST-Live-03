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

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    StockRepository stockRepository;

    @Autowired
    ModelMapper modelMapper;

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
    public StockDto save(StockDto stockDto) {
        stockDto.setIdentifier(stockDto.getProduct() + stockDto.getWarehouse());
        String product = stockDto.getIdentifier();
        Stock existingProduct = stockRepository.findByIdentifier(product);
        if (existingProduct != null) {
            stockDto.setMessage("Product - " + product + " already exists");
            stockDto.setSuccess(false);
            return stockDto;
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        stockRepository.save(stock);
        return stockDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        stockRepository.deleteByIdentifier(identifier);
    }

    @Override
    public StockDto update(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingProduct = stockRepository.findByIdentifier(identifier);
        if (existingProduct == null) {
            stockDto.setMessage("Stock with product - " + identifier + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }
        modelMapper.map(stockDto, existingProduct);
        stockRepository.save(existingProduct);
        return stockDto;
    }
}
