package com.ust.pos.stock.service.impl;

import com.ust.pos.dto.StockDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.StockService;
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
    public StockDto save(StockDto stockDto) {

        Stock existingStock = stockRepository.findByIdentifier(stockDto.getIdentifier());
        if (existingStock != null) {
            stockDto.setSuccess(false);
            stockDto.setMessage(
                    "Stock with Identifier " + stockDto.getIdentifier() + " already exists!"
            );
            return stockDto;
        }

        Stock stock = modelMapper.map(stockDto, Stock.class);
        Stock savedStock = stockRepository.save(stock);

        StockDto responseDto = modelMapper.map(savedStock, StockDto.class);
        responseDto.setSuccess(true);
        responseDto.setMessage("Stock created successfully");

        return responseDto;
    }


    @Override
    public StockDto update(StockDto stockDto) {

        Stock stock = stockRepository.findById(stockDto.getId())
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        modelMapper.map(stockDto, stock);
        stockRepository.save(stock);
        StockDto responseDto = modelMapper.map(stock, StockDto.class);
        responseDto.setSuccess(true);
        responseDto.setMessage("Stock updated successfully");
        return responseDto;
    }

    @Override
    public void delete(Long id) {
        stockRepository.deleteById(id);
    }

    @Override
    public List<StockDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findAll(pageable);
        return modelMapper.map(stockPage.getContent(), listType);

    }

    @Override
    public StockDto findById(Long id) {

        return stockRepository.findById(id)
                .map(stock -> modelMapper.map(stock, StockDto.class))
                .orElseThrow(() -> new RuntimeException("Stock not found"));

    }

    @Override
    public StockDto changeStockStatus(String identifier, boolean status) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        stock.setStatus(status);
        stockRepository.save(stock);
        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        return modelMapper.map(stock, StockDto.class);
    }
}