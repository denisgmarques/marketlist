package com.marketlist.service.impl;

import com.marketlist.service.MarketListService;
import com.marketlist.domain.MarketList;
import com.marketlist.repository.MarketListRepository;
import com.marketlist.repository.search.MarketListSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing MarketList.
 */
@Service
@Transactional
public class MarketListServiceImpl implements MarketListService{

    private final Logger log = LoggerFactory.getLogger(MarketListServiceImpl.class);
    
    @Inject
    private MarketListRepository marketListRepository;
    
    @Inject
    private MarketListSearchRepository marketListSearchRepository;
    
    /**
     * Save a marketList.
     * 
     * @param marketList the entity to save
     * @return the persisted entity
     */
    public MarketList save(MarketList marketList) {
        log.debug("Request to save MarketList : {}", marketList);
        MarketList result = marketListRepository.save(marketList);
        marketListSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the marketLists.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<MarketList> findAll(Pageable pageable) {
        log.debug("Request to get all MarketLists");
        Page<MarketList> result = marketListRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one marketList by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public MarketList findOne(Long id) {
        log.debug("Request to get MarketList : {}", id);
        MarketList marketList = marketListRepository.findOne(id);
        return marketList;
    }

    /**
     *  Delete the  marketList by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete MarketList : {}", id);
        marketListRepository.delete(id);
        marketListSearchRepository.delete(id);
    }

    /**
     * Search for the marketList corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<MarketList> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MarketLists for query {}", query);
        return marketListSearchRepository.search(queryStringQuery(query), pageable);
    }
}
