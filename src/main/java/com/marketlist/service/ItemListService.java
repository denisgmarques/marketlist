package com.marketlist.service;

import com.marketlist.domain.ItemList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing ItemList.
 */
public interface ItemListService {

    /**
     * Save a itemList.
     * 
     * @param itemList the entity to save
     * @return the persisted entity
     */
    ItemList save(ItemList itemList);

    /**
     *  Get all the itemLists.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ItemList> findAll(Pageable pageable);

    /**
     *  Get the "id" itemList.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    ItemList findOne(Long id);

    /**
     *  Delete the "id" itemList.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the itemList corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<ItemList> search(String query, Pageable pageable);
}
