package com.marketlist.repository;

import com.marketlist.domain.ItemList;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ItemList entity.
 */
@SuppressWarnings("unused")
public interface ItemListRepository extends JpaRepository<ItemList,Long> {

}
