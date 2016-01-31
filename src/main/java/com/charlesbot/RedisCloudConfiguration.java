package com.charlesbot;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableRedisRepositories
public class RedisCloudConfiguration {

	@Value("${REDISCLOUD_URL}")
	private String redisUrl;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() throws URISyntaxException {

		// configure connection pooling
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(30);
		
		JedisConnectionFactory redis = new JedisConnectionFactory(poolConfig);
		
		URI redisUri = new URI(redisUrl);
		redis.setHostName(redisUri.getHost());
		redis.setPort(redisUri.getPort());
		redis.setPassword(redisUri.getUserInfo().split(":", 2)[1]);

		redis.setUsePool(true);
		
		redis.afterPropertiesSet();
		RedisConnection connection = redis.getConnection();

		return redis;
	}

	@Bean
	public RedisTemplate<?, ?> redisTemplate() throws URISyntaxException {
		RedisTemplate<byte[], byte[]> template = new RedisTemplate<byte[], byte[]>();
		template.setConnectionFactory(redisConnectionFactory());
		template.setEnableTransactionSupport(true);
		return template;
	}

}
