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
@Transactional
public class StockServiceImpl implements StockService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private StockRepository stockRepository;

    @Override
    public StockDto save(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);

        if (existingStock != null) {
            stockDto.setMessage("Stock with identifier - " + identifier + " already exists");
            stockDto.setSuccess(false);
            return stockDto;
        }

        Stock stock = modelMapper.map(stockDto, Stock.class);
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);

        if (existingStock == null) {
            stockDto.setMessage("Stock with identifier - " + identifier + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }

        modelMapper.map(stockDto, existingStock);
        stockRepository.save(existingStock);
        return stockDto;
    }

    @Override
    public void delete(String identifier) {
        stockRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<StockDto> findAll() {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        return modelMapper.map(stockRepository.findAll(), listType);
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifier(identifier), StockDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        if (stock != null) {
            stock.setStatus(!stock.isStatus());
            stockRepository.save(stock);
        }
    }

    @Override
    public List<StockDto> findAll(Pageable pageable) {
        Type listtype = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findAll(pageable);
        return modelMapper.map(stockPage.getContent(), listtype);
    }
}