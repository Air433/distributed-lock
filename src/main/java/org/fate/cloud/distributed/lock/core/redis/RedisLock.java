package org.fate.cloud.distributed.lock.core.redis;

import org.fate.cloud.distributed.lock.common.ThreadUtil;
import org.fate.cloud.distributed.lock.core.DistributedLock;
import org.fate.cloud.distributed.lock.core.factory.DistributedFactory;
import redis.clients.jedis.Jedis;

/**
 * @Author ouyanggang
 * @Date 2019/7/23 - 12:50
 */
public class RedisLock extends DistributedLock {

    private Jedis jedis = DistributedFactory.getJedis();

    @Override
    public void lock() {
        while (true){
            boolean result = RedisTool.tryGetDistributedLock(jedis, lockKey, lockRequestId, 30);
            if (result){
                isOpenExpirationRenewal = true;
                scheduleExpirationRenewal();
                break;
            }
            sleepBySecond(1);
        }
    }

    @Override
    public void unlock() {
        RedisTool.releaseDistributedLock(jedis, lockKey, lockRequestId);
        isOpenExpirationRenewal = false;
    }

    @Override
    protected void renewalTime() {
        Jedis jedis = DistributedFactory.getJedis();
        RedisTool.expirationRenewal(jedis, lockKey, lockRequestId, 30);
    }

    @Override
    protected void scheduleExpirationRenewal(){
        Runnable r = ()->{
            Jedis jedis = DistributedFactory.getJedis();
            while (isOpenExpirationRenewal){
                sleepBySecond(10);
                RedisTool.expirationRenewal(jedis, lockKey, lockRequestId, 30);
            }
        };
        ThreadUtil.submit(r);
    }

}
