package com.marketlist.repository.search;

import com.marketlist.domain.MarketList;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the MarketList entity.
 */
public interface MarketListSearchRepository extends ElasticsearchRepository<MarketList, Long> {
}
