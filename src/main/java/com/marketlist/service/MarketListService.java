package com.marketlist.service;

import com.marketlist.domain.MarketList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing MarketList.
 */
public interface MarketListService {

    /**
     * Save a marketList.
     * 
     * @param marketList the entity to save
     * @return the persisted entity
     */
    MarketList save(MarketList marketList);

    /**
     *  Get all the marketLists.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<MarketList> findAll(Pageable pageable);

    /**
     *  Get the "id" marketList.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    MarketList findOne(Long id);

    /**
     *  Delete the "id" marketList.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the marketList corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<MarketList> search(String query, Pageable pageable);
}
