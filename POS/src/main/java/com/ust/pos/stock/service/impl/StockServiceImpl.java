package com.ust.pos.stock.service.impl;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
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
    public StockDto save(StockDto stockDto) {
        Stock existingStock = stockRepository.findByIdentifier(stockDto.getIdentifier());
        if (existingStock != null) {
            stockDto.setMessage("Stock with identifier - " + stockDto.getIdentifier() + " already exists");
            stockDto.setSuccess(false);
            return stockDto;
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        if (pageable == null) {
            List<StockDto> stockDtoList = modelMapper.map(stockRepository.findAll(), listType);
            WsDto<StockDto> response = new WsDto<>();
            response.setDtoList(stockDtoList);
            response.setTotalRecords(stockDtoList.size());
            return response;
        }
        Page<Stock> stockPage = stockRepository.findAll(pageable);
        List<StockDto> stockDtoList = modelMapper.map(stockPage.getContent(), listType);
        WsDto<StockDto> wsDto = new WsDto<>();
        wsDto.setDtoList(stockDtoList);
        wsDto.setPage(stockPage.getNumber());
        wsDto.setSizePerPage(stockPage.getSize());
        wsDto.setTotalPages(stockPage.getTotalPages());
        wsDto.setTotalRecords(stockPage.getTotalElements());
        return wsDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        Stock existingStock = stockRepository.findByIdentifier(stockDto.getIdentifier());
        if (existingStock == null) {
            stockDto.setMessage("Stock with identifier - " + stockDto.getIdentifier() + "not found");
            stockDto.setSuccess(false);
            return stockDto;
        }
        modelMapper.map(stockDto, existingStock);
        stockRepository.save(existingStock);
        return stockDto;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifier(identifier), StockDto.class);
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        stockRepository.deleteByIdentifier(identifier);
    }

    @Override
    @Transactional
    public StockDto toggleStatus(String identifier, boolean status) {
        StockDto response = new StockDto();
        Stock stock = stockRepository.findByIdentifier(identifier);
        if (stock == null) {
            response.setSuccess(false);
            response.setMessage("Stock not found");
            return response;
        }
        stock.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }

}