package org.fate.cloud.distributed.lock.core.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.Collections;

/**
 * @Author ouyanggang
 * @Date 2019/7/23 - 14:24
 */
@Component
public class RedisTool {

    private static final String LOCK_SUCCESS = "OK";

    //NX ：只在键不存在时，才对键进行设置操作。 SET key value NX 效果等同于 SETNX key value 。
    //XX ：只在键已经存在时，才对键进行设置操作。
    private static final String SET_IF_NOT_EXIST = "NX";
    //EX second ：设置键的过期时间为 second 秒。 SET key value EX second 效果等同于 SETEX key second value
    //PX millisecond ：设置键的过期时间为 millisecond 毫秒。 SET key value PX millisecond 效果等同于 PSETEX key millisecond value
    private static final String SET_WITH_EXPIRE_TIME = "EX";

    private static final Long RELEASE_SUCCESS = 1L;

    public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime){
         String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);

        if (LOCK_SUCCESS.equals(result)){
            return true;
        }
        return false;
    }

    public static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId){
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        if (RELEASE_SUCCESS.equals(result)){
            return true;
        }
        return false;
    }

    public static void expirationRenewal(Jedis jedis,String lockKey, String requestId, int expireSeconds){
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then "
                +"return redis.call('expire', KEYS[1],ARGV[2]) else return 0 end ";
        jedis.eval(script, 1, lockKey, requestId, String.valueOf(expireSeconds));
    }
}
