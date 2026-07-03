package com.ust.pos.stock.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
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
public class StockServiceImpl extends CommonService implements StockService {
    
    private final ModelMapper modelMapper;

    private final StockRepository stockRepository;

    public StockServiceImpl(ModelMapper modelMapper, StockRepository stockRepository) {
        this.modelMapper = modelMapper;
        this.stockRepository = stockRepository;
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> userPage = stockRepository.findByDeletedFalse(pageable);

        WsDto<StockDto> userWsDto = new WsDto<>();
        userWsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        userWsDto.setTotalRecords(userPage.getTotalElements());
        userWsDto.setTotalPages(userPage.getTotalPages());
        userWsDto.setSizePerPage(pageable.getPageSize());
        userWsDto.setPage(pageable.getPageNumber());

        return userWsDto;
    }

    @Override
    public WsDto<StockDto> findAll(Specification<Stock> example, Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> page = stockRepository.findAll(example, pageable);

        WsDto<StockDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        softDelete(stock);
        setAuditFields(stock,false);
        stockRepository.save(stock) ;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifier(identifier), StockDto.class);
    }

    @Override
    public StockDto update(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);
        modelMapper.map(stockDto, existingStock);
        setAuditFields(existingStock,false);
        stockRepository.save(existingStock);
        return stockDto;
    }

    @Override
    public StockDto changeToggleStatus(String identifier, boolean status) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        if (stock != null) {
            stock.setStatus(status);
            stockRepository.save(stock);
        }
        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public List<StockDto> findActiveStatus() {
        List<Stock> allShelves = stockRepository.findAll();
        List<Stock> activeShelves = allShelves.stream().filter(Stock::isStatus).toList();

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        return modelMapper.map(activeShelves, listType);
    }

    @Override
    public StockDto save(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);
        if (existingStock != null) {
            if(existingStock.isDeleted()) {
                stockDto.setMessage("Stock with identifier - " + identifier + "has been soft deleted.(Rollback by changing status");
                stockDto.setSuccess(false);
                return stockDto;
            }
            stockDto.setMessage("Stock with identifier - " + identifier + " already exists");
            stockDto.setSuccess(false);
            return stockDto;
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        setAuditFields(stock, true);
        stockRepository.save(stock);
        return stockDto;
    }
}
