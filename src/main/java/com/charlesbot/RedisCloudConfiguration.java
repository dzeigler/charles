package com.charlesbot;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableRedisRepositories("com.charlesbot.model")
public class RedisCloudConfiguration {

	@Bean
	public RedisConnectionFactory redisConnectionFactory(@Value("${REDISCLOUD_URL}") final String redisUrl) throws URISyntaxException {

		// configure connection pooling
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(30);
		
		JedisConnectionFactory redis = new JedisConnectionFactory(poolConfig);
		
		URI redisUri = new URI(redisUrl);
		redis.getStandaloneConfiguration().setHostName(redisUri.getHost());
		redis.getStandaloneConfiguration().setPort(redisUri.getPort());
		redis.getStandaloneConfiguration().setPassword(redisUri.getUserInfo().split(":", 2)[1]);

		redis.setUsePool(true);
		
		redis.afterPropertiesSet();
		redis.getConnection();

		return redis;
	}

	@Bean
	public RedisTemplate<?, ?> redisTemplate(@Value("${REDISCLOUD_URL}") String redisUrl) throws URISyntaxException {
		RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory(redisUrl));
		template.setEnableTransactionSupport(true);
		return template;
	}

}
