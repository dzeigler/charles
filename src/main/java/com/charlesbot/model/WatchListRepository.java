package com.charlesbot.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface WatchListRepository extends CrudRepository<WatchList, String> {

	List<WatchList> findByUserId(String userId);

	WatchList findByUserIdAndName(String userId, String name);
	
}
