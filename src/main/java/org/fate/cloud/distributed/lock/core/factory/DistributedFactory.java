package org.fate.cloud.distributed.lock.core.factory;

import org.fate.cloud.distributed.lock.core.DistributedLock;
import org.fate.cloud.distributed.lock.core.redis.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;

/**
 * @Author ouyanggang
 * @Date 2019/7/24 - 11:30
 */
@Component
public class DistributedFactory {

    @Autowired
    private JedisPool pool;

    private static JedisPool jedisPool;
    @PostConstruct
    public void init(){
        jedisPool = pool;
    }

    public static DistributedLock getLock(){
        return new RedisLock();
    }

    public static Jedis getJedis(){
        return jedisPool.getResource();
    }
}
