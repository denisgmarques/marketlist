package com.marketlist.service.impl;

import com.marketlist.service.ItemListService;
import com.marketlist.domain.ItemList;
import com.marketlist.repository.ItemListRepository;
import com.marketlist.repository.search.ItemListSearchRepository;
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
 * Service Implementation for managing ItemList.
 */
@Service
@Transactional
public class ItemListServiceImpl implements ItemListService{

    private final Logger log = LoggerFactory.getLogger(ItemListServiceImpl.class);
    
    @Inject
    private ItemListRepository itemListRepository;
    
    @Inject
    private ItemListSearchRepository itemListSearchRepository;
    
    /**
     * Save a itemList.
     * 
     * @param itemList the entity to save
     * @return the persisted entity
     */
    public ItemList save(ItemList itemList) {
        log.debug("Request to save ItemList : {}", itemList);
        ItemList result = itemListRepository.save(itemList);
        itemListSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the itemLists.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ItemList> findAll(Pageable pageable) {
        log.debug("Request to get all ItemLists");
        Page<ItemList> result = itemListRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one itemList by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ItemList findOne(Long id) {
        log.debug("Request to get ItemList : {}", id);
        ItemList itemList = itemListRepository.findOne(id);
        return itemList;
    }

    /**
     *  Delete the  itemList by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ItemList : {}", id);
        itemListRepository.delete(id);
        itemListSearchRepository.delete(id);
    }

    /**
     * Search for the itemList corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ItemList> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ItemLists for query {}", query);
        return itemListSearchRepository.search(queryStringQuery(query), pageable);
    }
}
