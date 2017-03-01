package com.marketlist.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.marketlist.domain.ItemList;
import com.marketlist.service.ItemListService;
import com.marketlist.web.rest.util.HeaderUtil;
import com.marketlist.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ItemList.
 */
@RestController
@RequestMapping("/api")
public class ItemListResource {

    private final Logger log = LoggerFactory.getLogger(ItemListResource.class);
        
    @Inject
    private ItemListService itemListService;
    
    /**
     * POST  /item-lists : Create a new itemList.
     *
     * @param itemList the itemList to create
     * @return the ResponseEntity with status 201 (Created) and with body the new itemList, or with status 400 (Bad Request) if the itemList has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/item-lists",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ItemList> createItemList(@Valid @RequestBody ItemList itemList) throws URISyntaxException {
        log.debug("REST request to save ItemList : {}", itemList);
        if (itemList.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("itemList", "idexists", "A new itemList cannot already have an ID")).body(null);
        }
        ItemList result = itemListService.save(itemList);
        return ResponseEntity.created(new URI("/api/item-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("itemList", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /item-lists : Updates an existing itemList.
     *
     * @param itemList the itemList to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated itemList,
     * or with status 400 (Bad Request) if the itemList is not valid,
     * or with status 500 (Internal Server Error) if the itemList couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/item-lists",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ItemList> updateItemList(@Valid @RequestBody ItemList itemList) throws URISyntaxException {
        log.debug("REST request to update ItemList : {}", itemList);
        if (itemList.getId() == null) {
            return createItemList(itemList);
        }
        ItemList result = itemListService.save(itemList);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("itemList", itemList.getId().toString()))
            .body(result);
    }

    /**
     * GET  /item-lists : get all the itemLists.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of itemLists in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/item-lists",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ItemList>> getAllItemLists(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ItemLists");
        Page<ItemList> page = itemListService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/item-lists");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /item-lists/:id : get the "id" itemList.
     *
     * @param id the id of the itemList to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the itemList, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/item-lists/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ItemList> getItemList(@PathVariable Long id) {
        log.debug("REST request to get ItemList : {}", id);
        ItemList itemList = itemListService.findOne(id);
        return Optional.ofNullable(itemList)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /item-lists/:id : delete the "id" itemList.
     *
     * @param id the id of the itemList to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/item-lists/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteItemList(@PathVariable Long id) {
        log.debug("REST request to delete ItemList : {}", id);
        itemListService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("itemList", id.toString())).build();
    }

    /**
     * SEARCH  /_search/item-lists?query=:query : search for the itemList corresponding
     * to the query.
     *
     * @param query the query of the itemList search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/item-lists",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ItemList>> searchItemLists(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ItemLists for query {}", query);
        Page<ItemList> page = itemListService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/item-lists");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
