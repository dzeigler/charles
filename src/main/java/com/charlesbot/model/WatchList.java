package com.charlesbot.model;

import java.util.List;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RedisHash("watch_lists")
public class WatchList {

	@EqualsAndHashCode.Include
	@Id	public String id;
	@Indexed public String name;
	@Indexed public String userId;
	public List<Transaction> transactions;
	
}
