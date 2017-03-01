package com.marketlist.repository;

import com.marketlist.domain.MarketList;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the MarketList entity.
 */
@SuppressWarnings("unused")
public interface MarketListRepository extends JpaRepository<MarketList,Long> {

}
