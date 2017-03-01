package com.marketlist.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.marketlist.domain.MarketList;
import com.marketlist.service.MarketListService;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing MarketList.
 */
@RestController
@RequestMapping("/api")
public class MarketListResource {

    private final Logger log = LoggerFactory.getLogger(MarketListResource.class);
        
    @Inject
    private MarketListService marketListService;
    
    /**
     * POST  /market-lists : Create a new marketList.
     *
     * @param marketList the marketList to create
     * @return the ResponseEntity with status 201 (Created) and with body the new marketList, or with status 400 (Bad Request) if the marketList has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/market-lists",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MarketList> createMarketList(@RequestBody MarketList marketList) throws URISyntaxException {
        log.debug("REST request to save MarketList : {}", marketList);
        if (marketList.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("marketList", "idexists", "A new marketList cannot already have an ID")).body(null);
        }
        MarketList result = marketListService.save(marketList);
        return ResponseEntity.created(new URI("/api/market-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("marketList", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /market-lists : Updates an existing marketList.
     *
     * @param marketList the marketList to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated marketList,
     * or with status 400 (Bad Request) if the marketList is not valid,
     * or with status 500 (Internal Server Error) if the marketList couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/market-lists",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MarketList> updateMarketList(@RequestBody MarketList marketList) throws URISyntaxException {
        log.debug("REST request to update MarketList : {}", marketList);
        if (marketList.getId() == null) {
            return createMarketList(marketList);
        }
        MarketList result = marketListService.save(marketList);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("marketList", marketList.getId().toString()))
            .body(result);
    }

    /**
     * GET  /market-lists : get all the marketLists.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of marketLists in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/market-lists",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<MarketList>> getAllMarketLists(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of MarketLists");
        Page<MarketList> page = marketListService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/market-lists");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /market-lists/:id : get the "id" marketList.
     *
     * @param id the id of the marketList to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the marketList, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/market-lists/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MarketList> getMarketList(@PathVariable Long id) {
        log.debug("REST request to get MarketList : {}", id);
        MarketList marketList = marketListService.findOne(id);
        return Optional.ofNullable(marketList)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /market-lists/:id : delete the "id" marketList.
     *
     * @param id the id of the marketList to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/market-lists/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteMarketList(@PathVariable Long id) {
        log.debug("REST request to delete MarketList : {}", id);
        marketListService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("marketList", id.toString())).build();
    }

    /**
     * SEARCH  /_search/market-lists?query=:query : search for the marketList corresponding
     * to the query.
     *
     * @param query the query of the marketList search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/market-lists",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<MarketList>> searchMarketLists(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of MarketLists for query {}", query);
        Page<MarketList> page = marketListService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/market-lists");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
