package com.ust.pos.stock.service.impl;
import com.ust.pos.CommonService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
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
@Transactional
public class StockServiceImpl extends CommonService implements StockService {

    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public StockServiceImpl(StockRepository stockRepository, ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifier(identifier), StockDto.class);
    }

    @Override
    public StockDto save(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);
        if (existingStock != null) {
            if(existingStock.isDeleted()){
                stockDto.setMessage("Stock identifier - " + identifier + " not available");
                stockDto.setSuccess(false);
                return stockDto;
            }
            stockDto.setSuccess(false);
            stockDto.setMessage("Stock with identifier - " + identifier + " already exists");
            return stockDto;
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        setAuditFields(stock,true);
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
        setAuditFields(existingStock,false);
        stockRepository.save(existingStock);
        return stockDto;
    }

    @Override
    public void delete(String identifier) {
        Stock stock=stockRepository.findByIdentifier(identifier);
        softDelete(stock);
        setAuditFields(stock,false);
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {}.getType();
        Page<Stock> stockPage = stockRepository.findByIsDeletedFalse(pageable);
        WsDto<StockDto> stockDtoWsDto = new WsDto<>();
        stockDtoWsDto.setDtoList(modelMapper.map(stockPage.getContent(), listType));
        stockDtoWsDto.setTotalRecords(stockPage.getTotalElements());
        stockDtoWsDto.setTotalPages(stockPage.getTotalPages());
        stockDtoWsDto.setSizePerPage(pageable.getPageSize());
        stockDtoWsDto.setPage(pageable.getPageNumber());
        return stockDtoWsDto;
    }

    @Override
    public void toggleStatus(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        if (stock != null) {
            Boolean status = stock.getStatus();
            if (status == null) {
                status = false;
            }
            stock.setStatus(!status);
            stockRepository.save(stock);
        }
    }

    @Override
    public WsDto<StockDto> findAll(Specification<Stock> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> page = stockRepository.findAll(example, pageable);
        WsDto<StockDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }


}

