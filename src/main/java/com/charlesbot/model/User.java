package com.charlesbot.model;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("users")
public class User {

    @Id public String userId;
    public WatchList defaultWatchList;
    public List<WatchList> watchLists;

}
