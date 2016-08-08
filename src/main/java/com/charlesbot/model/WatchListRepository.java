package com.charlesbot.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchListRepository extends CrudRepository<WatchList, String> {

	List<WatchList> findByUserId(String userId);

	WatchList findByUserIdAndName(String userId, String name);
	
}
