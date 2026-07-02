package com.ust.pos.stocks.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.StocksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelfs;
import com.ust.pos.model.Stocks;
import com.ust.pos.model.StocksRepository;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stocks.service.StocksService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class StocksServiceImpl extends CommonService implements StocksService {

    private static final String STOCKS_WITH_IDENTIFIER = "Stocks with identifier - ";

    private final StocksRepository stocksRepository;

    private final ModelMapper modelMapper;

    private final ProductService productService;

    public StocksServiceImpl(StocksRepository stocksRepository, ModelMapper modelMapper, ProductService productService) {
        this.stocksRepository = stocksRepository;
        this.modelMapper = modelMapper;
        this.productService = productService;
    }

    @Override
    public StocksDto findByIdentifier(String identifier) {
        return modelMapper.map(stocksRepository.findByIdentifier(identifier), StocksDto.class);
    }

    @Override
    public StocksDto save(StocksDto stocksDto) {
        stocksDto.setIdentifier(stocksDto.getIdentifier().trim());
        String identifier = stocksDto.getIdentifier();
        Stocks existingStocks = stocksRepository.findByIdentifier(identifier);

        if (existingStocks != null) {
            if (!existingStocks.isDeleted()) {
                stocksDto.setMessage(STOCKS_WITH_IDENTIFIER + identifier + " already exists");
                stocksDto.setSuccess(false);
                return stocksDto;
            }
            stocksDto.setMessage(STOCKS_WITH_IDENTIFIER + identifier + " was previously deleted. " +
                    "Please contact backend team to restore.");
            stocksDto.setSuccess(false);
            return stocksDto;
        }
        Stocks stocks = modelMapper.map(stocksDto, Stocks.class);
        ProductDto productDto = productService.findByIdentifier(stocksDto.getIdentifier());
        stocks.setName(productDto.getName());
        setAuditFields(stocks,true);
        stocksRepository.save(stocks);
        stocksDto.setSuccess(true);
        stocksDto.setMessage("Stocks created successfully");
        return stocksDto;
    }

    @Override
    public StocksDto update(StocksDto stocksDto) {
        String identifier = stocksDto.getIdentifier();
        Stocks existingStocks = stocksRepository.findByIdentifier(identifier);
        if (existingStocks == null) {
            stocksDto.setMessage(STOCKS_WITH_IDENTIFIER + identifier + " not found");
            stocksDto.setSuccess(false);
            return stocksDto;
        }
        modelMapper.map(stocksDto, existingStocks);
        setAuditFields(existingStocks,false);
        stocksRepository.save(existingStocks);
        return stocksDto;
    }

    @Override
    public boolean delete(String identifier) {
        Stocks stocks = stocksRepository.findByIdentifier(identifier);
        if (stocks == null) return false;
        softDelete(stocks);
        setAuditFields(stocks,false);
        stocksRepository.save(stocks);
        return true;
    }

    @Override
    public WsDto<StocksDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StocksDto>>() {
        }.getType();
        Page<Stocks> stocksPage = stocksRepository.findByDeletedFalse(pageable);
        WsDto<StocksDto> stocksDtoWsDto = new WsDto<>();
        stocksDtoWsDto.setDtoList(modelMapper.map(stocksPage.getContent(), listType));
        stocksDtoWsDto.setTotalRecords(stocksPage.getTotalElements());
        stocksDtoWsDto.setTotalPages(stocksPage.getTotalPages());
        stocksDtoWsDto.setSizePerPage(pageable.getPageSize());
        stocksDtoWsDto.setPage(pageable.getPageNumber());
        return stocksDtoWsDto;
    }

    @Override
    public List<StocksDto> findIfTrue() {
        Type listType = new TypeToken<List<StocksDto>>() {
        }.getType();
        return modelMapper.map(stocksRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }

    @Override
    public StocksDto toggleStatus(String identifier) {
        Stocks stocks = stocksRepository.findByIdentifier(identifier);
        stocks.setStatus(!stocks.isStatus());
        setAuditFields(stocks,false);
        stocksRepository.save(stocks);
        return modelMapper.map(stocks, StocksDto.class);
    }

    @Override
    public WsDto<StocksDto> findAll(Specification<Stocks> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<StocksDto>>() {
        }.getType();
        Page<Stocks>  stocksPage= stocksRepository.findAll(example, pageable);
        WsDto<StocksDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(stocksPage.getContent(), listType));
        wsDto.setTotalRecords(stocksPage.getTotalElements());
        wsDto.setTotalPages(stocksPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}