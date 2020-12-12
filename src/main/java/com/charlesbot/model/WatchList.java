package com.charlesbot.model;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("watch_lists")
public class WatchList {

	@Id	public String id;
	@Indexed public String name;
	@Indexed public String userId;
	public List<Transaction> transactions;
	
}
