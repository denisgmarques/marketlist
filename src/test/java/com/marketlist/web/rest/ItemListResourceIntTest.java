package com.marketlist.web.rest;

import com.marketlist.MarketlistApp;
import com.marketlist.domain.ItemList;
import com.marketlist.repository.ItemListRepository;
import com.marketlist.service.ItemListService;
import com.marketlist.repository.search.ItemListSearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ItemListResource REST controller.
 *
 * @see ItemListResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MarketlistApp.class)
@WebAppConfiguration
@IntegrationTest
public class ItemListResourceIntTest {

    private static final String DEFAULT_DESCRIPTION = "AA";
    private static final String UPDATED_DESCRIPTION = "BB";

    @Inject
    private ItemListRepository itemListRepository;

    @Inject
    private ItemListService itemListService;

    @Inject
    private ItemListSearchRepository itemListSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restItemListMockMvc;

    private ItemList itemList;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ItemListResource itemListResource = new ItemListResource();
        ReflectionTestUtils.setField(itemListResource, "itemListService", itemListService);
        this.restItemListMockMvc = MockMvcBuilders.standaloneSetup(itemListResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        itemListSearchRepository.deleteAll();
        itemList = new ItemList();
        itemList.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createItemList() throws Exception {
        int databaseSizeBeforeCreate = itemListRepository.findAll().size();

        // Create the ItemList

        restItemListMockMvc.perform(post("/api/item-lists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(itemList)))
                .andExpect(status().isCreated());

        // Validate the ItemList in the database
        List<ItemList> itemLists = itemListRepository.findAll();
        assertThat(itemLists).hasSize(databaseSizeBeforeCreate + 1);
        ItemList testItemList = itemLists.get(itemLists.size() - 1);
        assertThat(testItemList.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the ItemList in ElasticSearch
        ItemList itemListEs = itemListSearchRepository.findOne(testItemList.getId());
        assertThat(itemListEs).isEqualToComparingFieldByField(testItemList);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = itemListRepository.findAll().size();
        // set the field null
        itemList.setDescription(null);

        // Create the ItemList, which fails.

        restItemListMockMvc.perform(post("/api/item-lists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(itemList)))
                .andExpect(status().isBadRequest());

        List<ItemList> itemLists = itemListRepository.findAll();
        assertThat(itemLists).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllItemLists() throws Exception {
        // Initialize the database
        itemListRepository.saveAndFlush(itemList);

        // Get all the itemLists
        restItemListMockMvc.perform(get("/api/item-lists?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(itemList.getId().intValue())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getItemList() throws Exception {
        // Initialize the database
        itemListRepository.saveAndFlush(itemList);

        // Get the itemList
        restItemListMockMvc.perform(get("/api/item-lists/{id}", itemList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(itemList.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingItemList() throws Exception {
        // Get the itemList
        restItemListMockMvc.perform(get("/api/item-lists/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateItemList() throws Exception {
        // Initialize the database
        itemListService.save(itemList);

        int databaseSizeBeforeUpdate = itemListRepository.findAll().size();

        // Update the itemList
        ItemList updatedItemList = new ItemList();
        updatedItemList.setId(itemList.getId());
        updatedItemList.setDescription(UPDATED_DESCRIPTION);

        restItemListMockMvc.perform(put("/api/item-lists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedItemList)))
                .andExpect(status().isOk());

        // Validate the ItemList in the database
        List<ItemList> itemLists = itemListRepository.findAll();
        assertThat(itemLists).hasSize(databaseSizeBeforeUpdate);
        ItemList testItemList = itemLists.get(itemLists.size() - 1);
        assertThat(testItemList.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the ItemList in ElasticSearch
        ItemList itemListEs = itemListSearchRepository.findOne(testItemList.getId());
        assertThat(itemListEs).isEqualToComparingFieldByField(testItemList);
    }

    @Test
    @Transactional
    public void deleteItemList() throws Exception {
        // Initialize the database
        itemListService.save(itemList);

        int databaseSizeBeforeDelete = itemListRepository.findAll().size();

        // Get the itemList
        restItemListMockMvc.perform(delete("/api/item-lists/{id}", itemList.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean itemListExistsInEs = itemListSearchRepository.exists(itemList.getId());
        assertThat(itemListExistsInEs).isFalse();

        // Validate the database is empty
        List<ItemList> itemLists = itemListRepository.findAll();
        assertThat(itemLists).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchItemList() throws Exception {
        // Initialize the database
        itemListService.save(itemList);

        // Search the itemList
        restItemListMockMvc.perform(get("/api/_search/item-lists?query=id:" + itemList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(itemList.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}
