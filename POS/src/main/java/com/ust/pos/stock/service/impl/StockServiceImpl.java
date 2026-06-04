package com.ust.pos.stock.service.impl;

import com.ust.pos.dto.StockDto;
import com.ust.pos.model.ProductRepository;
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
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        if (pageable == null) {
            return modelMapper.map(stockRepository.findAll(), listType);
        }
        Page<Stock> stockPage = stockRepository.findAll(pageable);
        return modelMapper.map(stockPage.getContent(), listType);
    }

    public StockDto save(StockDto stockDto) {
        boolean exists = productRepository.existsByIdentifier(stockDto.getProduct());

        if (!exists) {
            StockDto response = new StockDto();
            response.setMessage("Product not found");
            response.setSuccess(false);
            return response;
        }

        stockDto.setIdentifier(stockDto.getProduct() + stockDto.getWarehouse());
        Stock existingStock = stockRepository.findByIdentifier(stockDto.getIdentifier());
        if (existingStock != null) {
            StockDto response = new StockDto();
            response.setMessage("Already exists");
            response.setSuccess(false);
            return response;
        }

        Stock stock = modelMapper.map(stockDto, Stock.class);
        Stock savedStock = stockRepository.save(stock);

        StockDto response = modelMapper.map(savedStock, StockDto.class);
        response.setMessage("Successfully added the stock");
        response.setSuccess(true);

        return response;
    }

    public StockDto update(StockDto stockDto) {
        Stock existingStock = stockRepository.findById(stockDto.getId()).orElse(null);

        if (existingStock == null) {
            stockDto.setMessage("Stock - " + stockDto.getIdentifier() + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }

        String stockName = stockDto.getIdentifier();
        boolean isStockNameChanged = !stockName.equalsIgnoreCase(existingStock.getIdentifier());

        if (isStockNameChanged && productRepository.findByIdentifier(stockName) != null) {
            stockDto.setMessage("Product " + stockName + " already exists");
            stockDto.setSuccess(false);
            return stockDto;
        }

        stockDto.setMessage("Stock successfully edited");
        stockDto.setSuccess(true);
        modelMapper.map(stockDto, existingStock);
        stockRepository.save(existingStock);

        return stockDto;
    }

    public StockDto findById(long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        return modelMapper.map(stock, StockDto.class);
    }

    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifier(identifier), StockDto.class);
    }

    @Override
    public void delete(String identifier) {
        stockRepository.deleteByIdentifier(identifier);
    }
}
