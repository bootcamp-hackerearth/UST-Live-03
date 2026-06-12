package com.ust.pos.stock.service.impl;
import com.ust.pos.dto.PageDto;
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
    private StockRepository stockRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public StockDto save(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock =stockRepository.findByIdentifier(identifier);
        if (existingStock != null) {
            stockDto.setMessage("Stock with identifier - " + identifier + " already exists");
            stockDto.setSuccess(false);
            return stockDto;
        }
        Stock stock= modelMapper.map(stockDto, Stock.class);
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        String identifier =stockDto.getIdentifier();
        Stock existingStock =stockRepository.findByIdentifier(identifier);
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
    @Transactional
    public void delete(String identifier) {
      stockRepository.deleteByIdentifier(identifier);

    }

    @Override
    public PageDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findAll(pageable);
        PageDto<StockDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(stockPage.getContent(), listType));
        pageDto.setTotalRecords(stockPage.getTotalElements());
        pageDto.setTotalPages(stockPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifier(identifier), StockDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        if (stock != null) {
            boolean currentStatus = Boolean.TRUE.equals(stock.getStatus());
            stock.setStatus(!currentStatus);

            stockRepository.save(stock);
        }
    }
}
