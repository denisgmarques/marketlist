package com.marketlist.repository.search;

import com.marketlist.domain.ItemList;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ItemList entity.
 */
public interface ItemListSearchRepository extends ElasticsearchRepository<ItemList, Long> {
}
