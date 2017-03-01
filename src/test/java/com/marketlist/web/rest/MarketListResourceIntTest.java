package com.marketlist.web.rest;

import com.marketlist.MarketlistApp;
import com.marketlist.domain.MarketList;
import com.marketlist.repository.MarketListRepository;
import com.marketlist.service.MarketListService;
import com.marketlist.repository.search.MarketListSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the MarketListResource REST controller.
 *
 * @see MarketListResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MarketlistApp.class)
@WebAppConfiguration
@IntegrationTest
public class MarketListResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CREATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_CREATED_DATE_STR = dateTimeFormatter.format(DEFAULT_CREATED_DATE);

    @Inject
    private MarketListRepository marketListRepository;

    @Inject
    private MarketListService marketListService;

    @Inject
    private MarketListSearchRepository marketListSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restMarketListMockMvc;

    private MarketList marketList;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MarketListResource marketListResource = new MarketListResource();
        ReflectionTestUtils.setField(marketListResource, "marketListService", marketListService);
        this.restMarketListMockMvc = MockMvcBuilders.standaloneSetup(marketListResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        marketListSearchRepository.deleteAll();
        marketList = new MarketList();
        marketList.setName(DEFAULT_NAME);
        marketList.setCreatedDate(DEFAULT_CREATED_DATE);
    }

    @Test
    @Transactional
    public void createMarketList() throws Exception {
        int databaseSizeBeforeCreate = marketListRepository.findAll().size();

        // Create the MarketList

        restMarketListMockMvc.perform(post("/api/market-lists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(marketList)))
                .andExpect(status().isCreated());

        // Validate the MarketList in the database
        List<MarketList> marketLists = marketListRepository.findAll();
        assertThat(marketLists).hasSize(databaseSizeBeforeCreate + 1);
        MarketList testMarketList = marketLists.get(marketLists.size() - 1);
        assertThat(testMarketList.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMarketList.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);

        // Validate the MarketList in ElasticSearch
        MarketList marketListEs = marketListSearchRepository.findOne(testMarketList.getId());
        assertThat(marketListEs).isEqualToComparingFieldByField(testMarketList);
    }

    @Test
    @Transactional
    public void getAllMarketLists() throws Exception {
        // Initialize the database
        marketListRepository.saveAndFlush(marketList);

        // Get all the marketLists
        restMarketListMockMvc.perform(get("/api/market-lists?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(marketList.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE_STR)));
    }

    @Test
    @Transactional
    public void getMarketList() throws Exception {
        // Initialize the database
        marketListRepository.saveAndFlush(marketList);

        // Get the marketList
        restMarketListMockMvc.perform(get("/api/market-lists/{id}", marketList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(marketList.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingMarketList() throws Exception {
        // Get the marketList
        restMarketListMockMvc.perform(get("/api/market-lists/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMarketList() throws Exception {
        // Initialize the database
        marketListService.save(marketList);

        int databaseSizeBeforeUpdate = marketListRepository.findAll().size();

        // Update the marketList
        MarketList updatedMarketList = new MarketList();
        updatedMarketList.setId(marketList.getId());
        updatedMarketList.setName(UPDATED_NAME);
        updatedMarketList.setCreatedDate(UPDATED_CREATED_DATE);

        restMarketListMockMvc.perform(put("/api/market-lists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedMarketList)))
                .andExpect(status().isOk());

        // Validate the MarketList in the database
        List<MarketList> marketLists = marketListRepository.findAll();
        assertThat(marketLists).hasSize(databaseSizeBeforeUpdate);
        MarketList testMarketList = marketLists.get(marketLists.size() - 1);
        assertThat(testMarketList.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMarketList.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);

        // Validate the MarketList in ElasticSearch
        MarketList marketListEs = marketListSearchRepository.findOne(testMarketList.getId());
        assertThat(marketListEs).isEqualToComparingFieldByField(testMarketList);
    }

    @Test
    @Transactional
    public void deleteMarketList() throws Exception {
        // Initialize the database
        marketListService.save(marketList);

        int databaseSizeBeforeDelete = marketListRepository.findAll().size();

        // Get the marketList
        restMarketListMockMvc.perform(delete("/api/market-lists/{id}", marketList.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean marketListExistsInEs = marketListSearchRepository.exists(marketList.getId());
        assertThat(marketListExistsInEs).isFalse();

        // Validate the database is empty
        List<MarketList> marketLists = marketListRepository.findAll();
        assertThat(marketLists).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchMarketList() throws Exception {
        // Initialize the database
        marketListService.save(marketList);

        // Search the marketList
        restMarketListMockMvc.perform(get("/api/_search/market-lists?query=id:" + marketList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(marketList.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE_STR)));
    }
}
