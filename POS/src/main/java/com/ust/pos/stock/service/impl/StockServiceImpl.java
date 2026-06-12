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

        Stock stock = modelMapper.map(stockDto, Stock.class);
        Stock savedStock = stockRepository.save(stock);

        StockDto response = modelMapper.map(savedStock, StockDto.class);
        response.setMessage("Successfully added the stock");
        response.setSuccess(true);

        return response;
    }

    public StockDto update(StockDto stockDto) {

        Stock existingStock = stockRepository.findById(stockDto.getId())
                .orElseThrow(() ->
                        new RuntimeException("Stock not found with id: " + stockDto.getId()));

        modelMapper.map(stockDto, existingStock);

        Stock updatedStock = stockRepository.save(existingStock);

        StockDto response = modelMapper.map(updatedStock, StockDto.class);
        response.setMessage("Stock updated successfully");
        response.setSuccess(true);

        return response;
    }

    public StockDto findById(long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public void delete(long id) {
        stockRepository.deleteById(id);
    }
}
