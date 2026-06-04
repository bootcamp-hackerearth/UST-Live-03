package com.ust.pos.stocks.service.impl;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.StocksDto;
import com.ust.pos.model.Stocks;
import com.ust.pos.model.StocksRepository;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stocks.service.StocksService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class StocksServiceImpl implements StocksService {
    @Autowired
    private StocksRepository stocksRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductService productService;


    @Override
    public StocksDto findByIdentifier(String identifier) {
        return modelMapper.map(stocksRepository.findByIdentifier(identifier), StocksDto.class);
    }

    @Override
    public StocksDto save(StocksDto stocksDto) {
        String identifier = stocksDto.getIdentifier();
        Stocks existingStocks = stocksRepository.findByIdentifier(identifier);

        if (existingStocks != null) {
            stocksDto.setMessage("Stocks with identifier - " + identifier + " already exists");
            stocksDto.setSuccess(false);
            return stocksDto;
        }
        Stocks stocks = modelMapper.map(stocksDto, Stocks.class);
        ProductDto productDto = productService.findByIdentifier(stocksDto.getIdentifier());
        stocks.setName(productDto.getName());
        stocksRepository.save(stocks);
        return stocksDto;
    }

    @Override
    public StocksDto update(StocksDto stocksDto) {
        String identifier = stocksDto.getIdentifier();
        Stocks existingStocks = stocksRepository.findByIdentifier(identifier);
        if (existingStocks == null) {
            stocksDto.setMessage("Stocks with identifier - " + identifier + " not found");
            stocksDto.setSuccess(false);
            return stocksDto;
        }
        modelMapper.map(stocksDto, existingStocks);
        stocksRepository.save(existingStocks);
        return stocksDto;
    }

    @Override
    public boolean delete(String identifier) {
        stocksRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public List<StocksDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StocksDto>>() {
        }.getType();
        Page<Stocks> stocksPage = stocksRepository.findAll(pageable);
        return modelMapper.map(stocksPage.getContent(), listType);
    }

    @Override
    public List<StocksDto> findIfTrue() {
        Type listType = new TypeToken<List<StocksDto>>() {
        }.getType();
        return modelMapper.map(stocksRepository.findByStatusIsTrue(), listType);
    }

    @Override
    public StocksDto toggleStatus(String identifier) {
        Stocks stocks = stocksRepository.findByIdentifier(identifier);
        stocks.setStatus(!stocks.isStatus());
        stocksRepository.save(stocks);
        return modelMapper.map(stocks, StocksDto.class);
    }

}