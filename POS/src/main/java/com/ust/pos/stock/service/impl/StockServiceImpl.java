package com.ust.pos.stock.service.impl;
import com.ust.pos.common.CommonService;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.model.Shelfs;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.StockService;
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

    private final StockRepository stockRepository;

    private final ModelMapper modelMapper;

    public StockServiceImpl(StockRepository stockRepository, ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public StockDto save(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock =stockRepository.findByIdentifier(identifier);
        if (existingStock != null) {
            if (Boolean.TRUE.equals(existingStock.getDeleted())) {
                stockDto.setMessage("Shelfs with identifier - " + identifier + " has been soft deleted. Restore it by changing status.");
                stockDto.setSuccess(false);
                return stockDto;
            }
            stockDto.setMessage("Stock with identifier - " + identifier + " already exists");
            stockDto.setSuccess(false);
            return stockDto;
        }
        Stock stock= modelMapper.map(stockDto, Stock.class);
        stock.setDeleted(false);
        stock.setStatus(true);
        setAuditFields(stock,true);
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
        setAuditFields(existingStock,false);
       stockRepository.save(existingStock);
        return stockDto;
    }

    @Override
    public boolean delete(String identifier) {

        Stock stock = stockRepository.findByIdentifier(identifier);

        if (stock == null) {
            return false;
        }
        softDelete(stock);
        setAuditFields(stock, false);
        stockRepository.save(stock);
        return true;
    }

    @Override
    public PageDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findByDeletedFalse(pageable);
        PageDto<StockDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(stockPage.getContent(), listType));
        pageDto.setTotalRecords(stockPage.getTotalElements());
        pageDto.setTotalPages(stockPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public PageDto<StockDto> findAll(Specification<Stock> spec, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findAll(spec, pageable);
        PageDto<StockDto> PageDto = new PageDto<>();
        PageDto.setDtoList(modelMapper.map(stockPage.getContent(), listType));
        PageDto.setTotalRecords(stockPage.getTotalElements());
        PageDto.setTotalPages(stockPage.getTotalPages());
        PageDto.setSizePerPage(pageable.getPageSize());
        PageDto.setPage(pageable.getPageNumber());
        PageDto.setKeyword(keyword);
        return PageDto;
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

    @Override
    public List<StockDto> findActiveStocks() {
        Type listType = new TypeToken<List<StockDto>>() {}.getType();
        return modelMapper.map(stockRepository.findByStatusTrue(),listType);
    }
}
